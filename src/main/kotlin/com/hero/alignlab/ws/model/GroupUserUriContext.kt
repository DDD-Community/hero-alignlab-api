package com.hero.alignlab.ws.model

import com.hero.alignlab.domain.auth.model.AUTH_TOKEN_KEY
import com.hero.alignlab.domain.auth.model.AuthUserToken
import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.NotFoundException
import org.springframework.web.reactive.socket.WebSocketSession

data class GroupUserUriContext(
    val groupId: Long,
    val token: AuthUserToken,
) {
    companion object {
        fun from(session: WebSocketSession): GroupUserUriContext {
            val groupId = session.handshakeInfo.uri.path
                .split("/")
                .lastOrNull { it.matches(Regex("\\d+")) }
                ?.toLongOrNull() ?: throw NotFoundException(ErrorCode.NOT_FOUND_GROUP_ID_ERROR)

            val queryParams = session.handshakeInfo.uri.query
                .split("&")
                .associate { splitParams ->
                    val (key, value) = splitParams.split("=")
                    key to value
                }

            val token = queryParams[AUTH_TOKEN_KEY] ?: throw NotFoundException(ErrorCode.NOT_FOUND_TOKEN_ERROR)

            return GroupUserUriContext(
                groupId = groupId,
                token = AuthUserToken.from(token)
            )
        }
    }
}
