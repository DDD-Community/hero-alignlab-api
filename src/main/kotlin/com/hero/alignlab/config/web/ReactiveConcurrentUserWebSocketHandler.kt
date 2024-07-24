package com.hero.alignlab.config.web

import com.hero.alignlab.common.extension.mapper
import com.hero.alignlab.domain.auth.application.AuthFacade
import com.hero.alignlab.domain.auth.model.AUTH_TOKEN_KEY
import com.hero.alignlab.domain.auth.model.AuthUserToken
import com.hero.alignlab.domain.group.application.GroupUserService
import com.hero.alignlab.domain.group.infrastructure.GroupUserRepository
import com.hero.alignlab.domain.user.application.UserInfoService
import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.NotFoundException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.LocalDateTime

// rsocket의 경우, client의 requester 처리에 있어 부가적인 설정이 필요. 그러므로 reactive websocket으로 진행
// poc 용도의 handler, 리펙토링 예정
@Component
class ReactiveConcurrentUserWebSocketHandler(
    private val authFacade: AuthFacade,
    private val userInfoService: UserInfoService,
    private val groupUserService: GroupUserService,
    private val groupUserRepository: GroupUserRepository,
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

    private val eventFlux: Flux<String> = Flux.generate { sink ->
        try {
            sink.next(mapper.writeValueAsString("ping pong"))
        } catch (e: Exception) {
            sink.error(e)
        }
    }

    override fun handle(session: WebSocketSession): Mono<Void> {
        val authUserToken = session.handshakeInfo.headers
            .filter { header -> isTokenHeader(header.key) }
            .mapNotNull { header ->
                header.value
                    .firstOrNull()
                    ?.takeIf { token -> token.isNotBlank() }
                    ?.let { token -> AuthUserToken.from(token) }
            }.firstOrNull() ?: throw NotFoundException(ErrorCode.NOT_FOUND_TOKEN_ERROR)

        val user = authFacade.resolveAuthUser(authUserToken)

        val groupUsers = groupUserService.findAllByUidSync(user.uid)

        groupUsers.forEach { groupUser ->
            val cuser = concurrentUserMap[groupUser.groupId] ?: mutableMapOf()

            cuser[groupUser.uid] = session

            concurrentUserMap[groupUser.groupId] = cuser
        }

        logger.info { "concurrent user ${user.uid}" }

        concurrentUserMap.forEach { groupId, uidBySession ->
            uidBySession[user.uid] ?: return@forEach

            val uids = uidBySession.keys

            val userInfoByUid = userInfoService.findAllByIds(uids.toList()).associateBy { it.id }

            val groupUserss = groupUserService.findAllByGroupIdAndUids(groupId, userInfoByUid.keys)
                .associateBy { it.uid }

            uidBySession.forEach { (uid, websocketSession) ->
                val message = ConcurrentMessage(
                    groupId = groupId,
                    groupUsers = userInfoByUid.mapNotNull { (uid, info) ->
                        val groupUSer = groupUserss[uid] ?: return@mapNotNull null

                        ConcurrentMessage.ConcurrentUser(
                            groupUserId = groupUSer.id,
                            uid = uid,
                            nickname = info.nickname
                        )
                    }
                )

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
            .doFinally {
                handleSessionTermination(session, user.uid)
            }
            .then()
    }

    private fun isTokenHeader(headerKey: String): Boolean {
        return AUTH_TOKEN_KEY.equals(headerKey, ignoreCase = true)
    }

    private fun extractIdFromUri(uri: String): String? {
        // Regex to match the ID part in the URI
        val regex = Regex("/ws/v1/groups/(\\w+)/concurrent-users")
        return regex.find(uri)?.groupValues?.get(1)
    }

    private fun removeSessionFromMap(session: WebSocketSession) {
        concurrentUserMap.forEach { (groupId, uidBySession) ->
            val userToRemove = uidBySession.filterValues { it == session }.keys.firstOrNull()
            if (userToRemove != null) {
                uidBySession.remove(userToRemove)
                logger.info { "Removed session for user $userToRemove from group $groupId" }
                if (uidBySession.isEmpty()) {
                    concurrentUserMap.remove(groupId)
                    logger.info { "Removed group $groupId as it has no more users." }
                }
            }
        }
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
                    // Send the updated group status to remaining users
                    sendUpdatedGroupStatus(groupId, uidBySession)
                }
            }
        }
    }

    private fun sendUpdatedGroupStatus(groupId: Long, uidBySession: MutableMap<Long, WebSocketSession>) {
        val uids = uidBySession.keys
        val userInfoByUid = userInfoService.findAllByIds(uids.toList()).associateBy { it.id }

        val groupUsers = groupUserService.findAllByGroupIdAndUids(groupId, userInfoByUid.keys)
            .associateBy { it.uid }

        val message = ConcurrentMessage(
            groupId = groupId,
            groupUsers = userInfoByUid.mapNotNull { (uid, info) ->
                val groupUser = groupUsers[uid] ?: return@mapNotNull null

                ConcurrentMessage.ConcurrentUser(
                    groupUserId = groupUser.id,
                    uid = uid,
                    nickname = info.nickname
                )
            }
        )

        uidBySession.forEach { (_, websocketSession) ->
            websocketSession
                .send(Mono.just(websocketSession.textMessage(mapper.writeValueAsString(message))))
                .subscribe()
        }
    }

    fun forceCloseAllSessions() {
        concurrentUserMap.forEach { (_, sessions) ->
            sessions.forEach { (_, session) ->
                session.close().subscribe()
            }
        }
        
        // 모든 세션이 종료된 후, 맵을 비울 수 있습니다.
        concurrentUserMap.clear()
    }
}

data class ConcurrentMessage(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val groupId: Long,
    val groupUsers: List<ConcurrentUser>
) {
    data class ConcurrentUser(
        val groupUserId: Long,
        val uid: Long,
        val nickname: String,
    )
}
