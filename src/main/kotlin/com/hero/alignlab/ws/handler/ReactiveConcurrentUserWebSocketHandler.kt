package com.hero.alignlab.ws.handler

import com.hero.alignlab.common.extension.mapper
import com.hero.alignlab.domain.auth.application.AuthFacade
import com.hero.alignlab.domain.auth.model.AUTH_TOKEN_KEY
import com.hero.alignlab.domain.auth.model.AuthUserToken
import com.hero.alignlab.domain.group.application.GroupUserScoreService
import com.hero.alignlab.domain.group.application.GroupUserService
import com.hero.alignlab.domain.user.application.UserInfoService
import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.NotFoundException
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
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Component
class ReactiveConcurrentUserWebSocketHandler(
    private val authFacade: AuthFacade,
    private val userInfoService: UserInfoService,
    private val groupUserService: GroupUserService,
    private val groupUserScoreService: GroupUserScoreService,
) : WebSocketHandler {
    private val logger = KotlinLogging.logger { }

    /**
     * redis는 현 상태에서 사용하지 않는다. 현재 스펙상 오버엔지니어링
     * - key : groupdId
     * - value
     *      - key : uid
     *      - value : WebSocketSession
     */
    private val concurrentUserByMap: ConcurrentMap<Long, ConcurrentMap<Long, WebSocketSession>> = ConcurrentHashMap()

    override fun handle(session: WebSocketSession): Mono<Void> {
        val groupId = session.handshakeInfo.uri.path
            .split("/")
            .lastOrNull { it.matches(Regex("\\d+")) }

        val queryParams = session.handshakeInfo.uri.query
            .split("&")
            .associate {
                val (key, value) = it.split("=")
                key to value
            }

        val token = queryParams[AUTH_TOKEN_KEY] ?: throw NotFoundException(ErrorCode.NOT_FOUND_TOKEN_ERROR)

        val authUserToken = AuthUserToken(AUTH_TOKEN_KEY, token)

        val user = authFacade.resolveAuthUser(authUserToken)

        val groupUsers = groupUserService.findAllByUidSync(user.uid)

        groupUsers.forEach { groupUser ->
            val cuser = concurrentUserByMap[groupUser.groupId] ?: ConcurrentHashMap()

            cuser[groupUser.uid] = session

            concurrentUserByMap[groupUser.groupId] = cuser
        }


        logger.info { "concurrent user ${user.uid}" }

        concurrentUserByMap.forEach { (groupId, uidBySession) ->
            uidBySession[user.uid] ?: return@forEach

            val uids = uidBySession.keys

            val userInfoByUid = userInfoService.findAllByIdsSync(uids.toList()).associateBy { it.id }
            val groupUserss = groupUserService.findAllByGroupIdAndUidsSync(groupId, userInfoByUid.keys)
                .associateBy { it.uid }
            val groupUserScores = groupUserScoreService.findAllByGroupIdAndUidsSync(groupId, userInfoByUid.keys)
                .associateBy { it.uid }

            uidBySession.forEach { (_, websocketSession) ->
                val message = ConcurrentMessage.of(groupId, userInfoByUid, groupUserss, groupUserScores)
                    .run { mapper.writeValueAsString(this) }

                websocketSession
                    .send(Mono.just(websocketSession.textMessage(message)))
                    .subscribe()
            }
        }

        return session.receive()
            .map(WebSocketMessage::getPayloadAsText)
            .flatMap { payload ->
                when {
                    payload.contains("ping") -> {
                        session.send(Mono.just(session.textMessage("pong")))
                    }

                    else -> {
                        logger.warn { "UNDEFINED WS MESSAGE : $payload" }
                        Mono.empty()
                    }
                }
            }
            .log()
            .doFinally { handleSessionTermination(session, user.uid) }
            .then()
    }

    private fun handleSessionTermination(session: WebSocketSession, uid: Long) {
        concurrentUserByMap.forEach { (groupId, uidBySession) ->
            val removedUser = uidBySession.remove(uid)

            if (removedUser != null) {
                logger.info { "Removed session for user $uid from group $groupId" }

                if (uidBySession.isEmpty()) {
                    concurrentUserByMap.remove(groupId)
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
        val groupUserScores = groupUserScoreService.findAllByGroupIdAndUidsSync(groupId, userInfoByUid.keys)
            .associateBy { it.uid }

        val message = ConcurrentMessage.of(groupId, userInfoByUid, groupUsers, groupUserScores)
            .run { mapper.writeValueAsString(this) }

        uidBySession.forEach { (_, websocketSession) ->
            websocketSession
                .send(Mono.just(websocketSession.textMessage(message)))
                .subscribe()
        }
    }

    fun forceCloseAllWebSocketSessions() {
        concurrentUserByMap.forEach { (_, webSocketSession) ->
            webSocketSession.forEach { (_, webSocketSession) ->
                webSocketSession
                    .close()
                    .subscribe()
            }
        }

        /** Websocket Session Release */
        concurrentUserByMap.clear()
    }
}
