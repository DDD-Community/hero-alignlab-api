package com.hero.alignlab.ws.handler

import com.hero.alignlab.domain.auth.application.AuthFacade
import com.hero.alignlab.domain.group.application.GroupUserService
import com.hero.alignlab.ws.model.GroupUserUriContext
import com.hero.alignlab.ws.model.WsGroupUserModel
import com.hero.alignlab.ws.service.GroupUserWsFacade
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap

@Component
class ReactiveGroupUserWebSocketHandler(
    private val authFacade: AuthFacade,
    private val groupUserService: GroupUserService,
    private val groupUserWsFacade: GroupUserWsFacade,
) : WebSocketHandler {
    private val logger = KotlinLogging.logger { }

    /**
     * redis는 현 상태에서 사용하지 않는다. 현재 스펙상 오버엔지니어링
     * - session 정보들을 local에 캐싱해두어 사용.
     * - key : groupdId
     * - value
     *      - key : uid
     *      - value : WebSocketSession
     */
    private val groupUserByGroupId: MutableMap<Long, MutableMap<Long, WebSocketSession>> = mutableMapOf()

    override fun handle(session: WebSocketSession): Mono<Void> {
        return mono {
            /** uri 정보를 파싱 */
            val uriContext = GroupUserUriContext.from(session)

            /** uri에 들어있는 Token 정보로, ws로 요청이 들어온 user를 조회 */
            val user = authFacade.resolveAuthUser(uriContext.token)

            /** user가 속해 있는 그룹 정보를 전체 조회 */
            val groupUsers = groupUserService.findAllByUid(user.uid)

            groupUsers.forEach { groupUser ->
                /** 현재 접속중인 그룹 유저들의 정보 */
                val groupUsersByUid = groupUserByGroupId[groupUser.groupId] ?: ConcurrentHashMap()

                /** ws를 요청한 사용자의 Session 정볼흘 업데이트 */
                groupUsersByUid[groupUser.uid] = session

                /** local 정보 최신화 */
                groupUserByGroupId[groupUser.groupId] = groupUsersByUid
            }

            groupUserByGroupId.forEach { (groupId, sessionByUid) ->
                /** ws 요청 사용자의 세션 */
                sessionByUid[user.uid] ?: return@forEach

                /** 홰당 사용자의 session에 소켓 메세지 발송 및 같은 그룹원에게 소켓 메세지 발송 */
                launchSendConnectEvent(
                    groupId = groupId,
                    sessionByUid = sessionByUid
                )
            }

            /** ws에 대한 ping-pong 및 종료 처리 로직 */
            session.receive()
                .map { message -> message.payloadAsText }
                .flatMap { payload -> checkPingPong(payload, session) }
                .log()
                .doFinally { handleSessionTermination(user.uid) }
                .then()
                .awaitSingleOrNull()
        }
    }

    private fun checkPingPong(
        payload: String,
        session: WebSocketSession
    ): Mono<Void> {
        return when (payload.contains("ping")) {
            true -> {
                session.send(Mono.just(session.textMessage("pong")))
            }

            false -> {
                logger.warn { "UNDEFINED WS MESSAGE : $payload" }
                Mono.empty()
            }
        }
    }

    private fun handleSessionTermination(uid: Long) {
        groupUserByGroupId.forEach { (groupId, uidBySession) ->
            val removedUser = uidBySession.remove(uid)

            if (removedUser != null) {
                logger.info { "Removed session for user $uid from group $groupId" }

                when (uidBySession.isEmpty()) {
                    true -> {
                        groupUserByGroupId.remove(groupId)
                        logger.info { "Removed group $groupId as it has no more users." }
                    }

                    false -> {
                        launchSendConnectEvent(groupId, uidBySession)
                    }
                }
            }
        }
    }

    fun forceCloseAllWebSocketSessions() {
        groupUserByGroupId.forEach { (_, session) ->
            session.forEach { (_, session) ->
                session.close().subscribe()
            }
        }

        /** Websocket Session Release */
        groupUserByGroupId.clear()
    }

    fun getWsGroupUsers(): List<WsGroupUserModel> {
        return groupUserByGroupId.map { (groupId, groupUsers) ->
            WsGroupUserModel(groupId, groupUsers.keys)
        }
    }

    /** 발송되는 순서가 중요하지 않다. */
    private fun launchSendConnectEvent(
        groupId: Long,
        sessionByUid: MutableMap<Long, WebSocketSession>,
    ) {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            sendConnectEvent(
                groupId = groupId,
                sessionByUid = sessionByUid
            )
        }
    }

    private suspend fun sendConnectEvent(
        groupId: Long,
        sessionByUid: MutableMap<Long, WebSocketSession>
    ) {
        sessionByUid.forEach { (uid, session) ->
            val eventMessage = groupUserWsFacade.generateEventMessage(
                uid = uid,
                groupId = groupId,
                spreadUids = sessionByUid.keys.toList()
            )

            session
                .send(Mono.just(session.textMessage(eventMessage.message())))
                .subscribe()
        }
    }

    fun launchSendStatusUpdateEventByGroupId(groupId: Long) {
        val sessions = groupUserByGroupId[groupId] ?: return

        launchSendConnectEvent(groupId, sessions)
    }

    /** 응원을 보낸 사람과 받은 사람에게 WS 알림 진행 */
    fun launchSendEventByCheerUp(actorUid: Long, targetUid: Long, groupId: Long) {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            /** 응원하기를 누른 action 대상자  */
            groupUserByGroupId[groupId]?.get(actorUid)?.let { session ->
                val eventMessage = groupUserWsFacade.generateEventMessage(
                    uid = actorUid,
                    groupId = groupId,
                    spreadUids = listOf(actorUid, targetUid),
                    cheerUpSenderUid = actorUid,
                )

                session
                    .send(Mono.just(session.textMessage(eventMessage.message())))
                    .subscribe()
            }

            /** 응원하기를 받은 대상자 */
            groupUserByGroupId[groupId]?.get(targetUid)?.let { session ->
                val eventMessage = groupUserWsFacade.generateEventMessage(
                    uid = targetUid,
                    groupId = groupId,
                    spreadUids = listOf(actorUid, targetUid),
                    cheerUpSenderUid = actorUid,
                )

                session
                    .send(Mono.just(session.textMessage(eventMessage.message())))
                    .subscribe()
            }
        }
    }
}
