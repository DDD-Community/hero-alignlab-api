package com.hero.alignlab.ws.handler

import com.hero.alignlab.common.extension.mapper
import com.hero.alignlab.domain.auth.application.AuthFacade
import com.hero.alignlab.domain.auth.model.AuthUserToken.Companion.resolve
import com.hero.alignlab.domain.group.application.GroupUserService
import com.hero.alignlab.domain.user.application.UserInfoService
import com.hero.alignlab.ws.model.ConcurrentMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.SynchronousSink
import java.time.Duration

@Component
class ReactiveConcurrentUserWebSocketHandler(
    private val authFacade: AuthFacade,
    private val userInfoService: UserInfoService,
    private val groupUserService: GroupUserService,
) : WebSocketHandler {
    private val logger = KotlinLogging.logger { }

    /**
     * redis는 현 상태에서 사용하지 않는다. 현재 스펙상 오버엔지니어링
     * - key : groupdId
     * - value
     *      - key : uid
     *      - value : WebSocketSession
     */
    private val concurrentUserMap: MutableMap<Long, MutableMap<Long, WebSocketSession>> = mutableMapOf()

    private val eventFlux: Flux<String> = Flux.generate { sink: SynchronousSink<String> ->
        runCatching {
            mapper.writeValueAsString("ping pong")
        }.onSuccess { json ->
            sink.next(json)
        }.onFailure { e ->
            sink.error(e)
        }
    }

    override fun handle(session: WebSocketSession): Mono<Void> {
        val authUserToken = session.handshakeInfo.headers.resolve()

        val user = authFacade.resolveAuthUser(authUserToken)

        val groupUsers = groupUserService.findAllByUidSync(user.uid)

        groupUsers.forEach { groupUser ->
            val cuser = concurrentUserMap[groupUser.groupId] ?: mutableMapOf()

            cuser[groupUser.uid] = session

            concurrentUserMap[groupUser.groupId] = cuser
        }

        logger.info { "concurrent user ${user.uid}" }

        concurrentUserMap.forEach { (groupId, uidBySession) ->
            uidBySession[user.uid] ?: return@forEach

            val uids = uidBySession.keys

            val userInfoByUid = userInfoService.findAllByIdsSync(uids.toList()).associateBy { it.id }

            val groupUserss = groupUserService.findAllByGroupIdAndUidsSync(groupId, userInfoByUid.keys)
                .associateBy { it.uid }

            uidBySession.forEach { (_, websocketSession) ->
                val message = ConcurrentMessage.of(groupId, userInfoByUid, groupUserss)

                websocketSession
                    .send(Mono.just(websocketSession.textMessage(mapper.writeValueAsString(message))))
                    .subscribe()
            }
        }

        return session.send(
            Flux.interval(Duration.ofMillis(1000L))
                .zipWith(eventFlux) { _, event -> event }
                .map(session::textMessage)
        ).and(session.receive().map(WebSocketMessage::getPayloadAsText).log())
            .doFinally { handleSessionTermination(session, user.uid) }
            .then()
    }

    private fun handleSessionTermination(session: WebSocketSession, uid: Long) {
        concurrentUserMap.forEach { (groupId, uidBySession) ->
            val removedUser = uidBySession.remove(uid)

            if (removedUser != null) {
                logger.info { "Removed session for user $uid from group $groupId" }

                if (uidBySession.isEmpty()) {
                    concurrentUserMap.remove(groupId)
                    logger.info { "Removed group $groupId as it has no more users." }
                } else {
                    CoroutineScope(Dispatchers.IO + Job()).launch {
                        sendUpdatedGroupStatus(groupId, uidBySession)
                    }
                }
            }
        }
    }

    private suspend fun sendUpdatedGroupStatus(groupId: Long, uidBySession: MutableMap<Long, WebSocketSession>) {
        val userInfoByUid = userInfoService.findAllByIds(uidBySession.keys.toList()).associateBy { it.id }
        val groupUsers = groupUserService.findAllByGroupIdAndUids(groupId, userInfoByUid.keys).associateBy { it.uid }

        val message = ConcurrentMessage.of(groupId, userInfoByUid, groupUsers)

        uidBySession.forEach { (_, websocketSession) ->
            websocketSession
                .send(Mono.just(websocketSession.textMessage(mapper.writeValueAsString(message))))
                .subscribe()
        }
    }

    fun forceCloseAllWebSocketSessions() {
        concurrentUserMap.forEach { (_, webSocketSession) ->
            webSocketSession.forEach { (_, webSocketSession) ->
                webSocketSession
                    .close()
                    .subscribe()
            }
        }

        /** Websocket Session Release */
        concurrentUserMap.clear()
    }
}
