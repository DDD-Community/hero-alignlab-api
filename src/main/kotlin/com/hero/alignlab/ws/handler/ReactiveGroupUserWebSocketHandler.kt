package com.hero.alignlab.ws.handler

import com.hero.alignlab.domain.auth.application.AuthFacade
import com.hero.alignlab.domain.group.application.GroupUserService
import com.hero.alignlab.ws.model.GroupUserUriContext
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
import java.util.concurrent.ConcurrentMap

@Component
class ReactiveGroupUserWebSocketHandler(
    private val authFacade: AuthFacade,
    private val groupUserService: GroupUserService,
    private val groupUserWsFacade: GroupUserWsFacade,
) : WebSocketHandler {
    private val logger = KotlinLogging.logger { }

    /**
     * redis는 현 상태에서 사용하지 않는다. 현재 스펙상 오버엔지니어링
     * - key : groupdId
     * - value
     *      - key : uid
     *      - value : WebSocketSession
     */
    private val groupUserByMap: ConcurrentMap<Long, ConcurrentMap<Long, WebSocketSession>> = ConcurrentHashMap()

    override fun handle(session: WebSocketSession): Mono<Void> {
        return mono {
            val uriContext = GroupUserUriContext.from(session)

            val user = authFacade.resolveAuthUser(uriContext.token)

            val groupUsers = groupUserService.findAllByUid(user.uid)

            groupUsers.forEach { groupUser ->
                val targetGroupUser = groupUserByMap[groupUser.groupId] ?: ConcurrentHashMap()

                targetGroupUser[groupUser.uid] = session

                groupUserByMap[groupUser.groupId] = targetGroupUser
            }

            groupUserByMap.forEach { (groupId, sessionByUid) ->
                sessionByUid[user.uid] ?: return@forEach

                launchSendEvent(user.uid, groupId, sessionByUid)
            }

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
        groupUserByMap.forEach { (groupId, uidBySession) ->
            val removedUser = uidBySession.remove(uid)

            if (removedUser != null) {
                logger.info { "Removed session for user $uid from group $groupId" }

                when (uidBySession.isEmpty()) {
                    true -> {
                        groupUserByMap.remove(groupId)
                        logger.info { "Removed group $groupId as it has no more users." }
                    }

                    false -> {
                        launchSendEvent(uid, groupId, uidBySession)
                    }
                }
            }
        }
    }

    fun launchSendEvent(uid: Long, groupId: Long) {
        groupUserByMap[groupId]?.let { groupUsers ->
            launchSendEvent(uid, groupId, groupUsers)
        }
    }

    /** 발송되는 순서가 중요하지 않다. */
    private fun launchSendEvent(
        uid: Long,
        groupId: Long,
        sessionByUid: ConcurrentMap<Long, WebSocketSession>
    ) {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            sendUpdatedGroupStatus(uid, groupId, sessionByUid)
        }
    }

    private suspend fun sendUpdatedGroupStatus(
        uid: Long,
        groupId: Long,
        sessionByUid: MutableMap<Long, WebSocketSession>
    ) {
        val eventMessage = sessionByUid.keys
            .toList()
            .let { uids -> groupUserWsFacade.generateEventMessage(uid, groupId, uids) }

        sessionByUid.forEach { (_, session) ->
            session
                .send(Mono.just(session.textMessage(eventMessage.message())))
                .subscribe()
        }
    }

    fun forceCloseAllWebSocketSessions() {
        groupUserByMap.forEach { (_, session) ->
            session.forEach { (_, session) ->
                session
                    .close()
                    .subscribe()
            }
        }

        /** Websocket Session Release */
        groupUserByMap.clear()
    }
}
