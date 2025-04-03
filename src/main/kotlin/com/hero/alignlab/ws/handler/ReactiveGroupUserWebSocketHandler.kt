package com.hero.alignlab.ws.handler

import com.hero.alignlab.domain.auth.application.AuthFacade
import com.hero.alignlab.domain.group.application.GroupUserService
import com.hero.alignlab.ws.model.GroupUserEventMessage
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

                /** 같은 그룹원에게 모니터링 신규 참여자에 대한 정보를 안내하기 위해 소켓 메세지 발송 */
                launchSendConnectEvent(
                    trace = GroupUserEventMessage.Trace(
                        action = "그룹 내 유저 모니터링 참여 (그룹원 전체에게 발송)",
                        rootUid = user.uid,
                        reason = "${user.uid}가 모니터링을 진행하게 되었고, 이에 따라 그룹[${groupId}] 전체인원에게 해당 사용자의 정보를 제공",
                    ),
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
                        launchSendConnectEvent(
                            trace = GroupUserEventMessage.Trace(
                                action = "그룹내 유저 모니터링 종료 (전체 그룹원에게 발송)",
                                rootUid = uid,
                                reason = "${uid}가 모니터링을 종료함에 따라, 그룹[${groupId}]에게 최신화된 접속 유저 정보를 반환"
                            ),
                            groupId = groupId,
                            sessionByUid = uidBySession
                        )
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
        trace: GroupUserEventMessage.Trace,
        groupId: Long,
        sessionByUid: MutableMap<Long, WebSocketSession>,
    ) {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            sessionByUid.forEach { (uid, session) ->
                val eventMessage = groupUserWsFacade.generateEventMessage(
                    trace = trace,
                    uid = uid,
                    groupId = groupId,
                    spreadUids = sessionByUid.keys.toList()
                )

                session
                    .send(Mono.just(session.textMessage(eventMessage.message())))
                    .subscribe()
            }
        }
    }

    fun launchSendStatusUpdateEventByGroupId(uid: Long, groupId: Long) {
        val sessions = groupUserByGroupId[groupId] ?: return

        launchSendConnectEvent(
            trace = GroupUserEventMessage.Trace(
                action = "포즈 촬영으로 인한 업데이트 발송 (전체 그룹원 발송)",
                rootUid = uid,
                reason = "POSE 촬영 진행 -> 그룹 정보 갱신 필요, 포즈 촬여을 한 uid: $uid"
            ),
            groupId = groupId,
            sessionByUid = sessions,
        )
    }

    fun launchSendGroupRankRefreshEventByGroupId(uid: Long, groupId: Long) {
        val sessions = groupUserByGroupId[groupId] ?: return

        launchSendConnectEvent(
            trace = GroupUserEventMessage.Trace(
                action = "그룹 랭킹 갱신으로 인해 발송 (전체 그룹원 발송)",
                rootUid = uid,
                reason = "3초 단위 그룹 랭킹 갱신 -> 그룹 정보 노출에 대한 갱신 필요, 그룹 랭킹이 변경된 uid: $uid"
            ),
            groupId = groupId,
            sessionByUid = sessions,
        )
    }

    /** 응원을 보낸 사람과 받은 사람에게 WS 알림 진행 */
    fun launchSendEventByCheerUp(actorUid: Long, targetUid: Long, groupId: Long) {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            val spreadUids = groupUserByGroupId[groupId]?.keys?.toList() ?: return@launch

            /** 응원하기를 누른 action 대상자  */
            groupUserByGroupId[groupId]?.get(actorUid)?.let { session ->
                val eventMessage = groupUserWsFacade.generateEventMessage(
                    trace = GroupUserEventMessage.Trace(
                        action = "응원하기를 보냄 (보낸 사용자에게만 발송)",
                        rootUid = actorUid,
                        reason = "${actorUid}가 응원하기를 누름, 응원하기를 보낸 대상은 [$targetUid] action을 진행한 사용자에게 최신 데이터를 제공하기 위해 메세지를 받음 "
                    ),
                    uid = actorUid,
                    groupId = groupId,
                    /** 현재 접속중인 사용자에 대한 정보는 전체 제공되어야 함 */
                    spreadUids = spreadUids,
                    cheerUpSenderUid = actorUid,
                )

                session
                    .send(Mono.just(session.textMessage(eventMessage.message())))
                    .subscribe()
            }

            /** 응원하기를 받은 대상자 */
            groupUserByGroupId[groupId]?.get(targetUid)?.let { session ->
                val eventMessage = groupUserWsFacade.generateEventMessage(
                    trace = GroupUserEventMessage.Trace(
                        action = "응원하기를 받음 (받은 사용자에게만 발송)",
                        rootUid = actorUid,
                        reason = "${actorUid}가 응원하기를 누름, 응원하기를 보낸 대상은 [$targetUid] 응원하기를 받았기 때문에 메세지를 받음"
                    ),
                    uid = targetUid,
                    groupId = groupId,
                    /** 현재 접속중인 사용자에 대한 정보는 전체 제공되어야 함 */
                    spreadUids = spreadUids,
                    cheerUpSenderUid = actorUid,
                )

                session
                    .send(Mono.just(session.textMessage(eventMessage.message())))
                    .subscribe()
            }
        }
    }
}
