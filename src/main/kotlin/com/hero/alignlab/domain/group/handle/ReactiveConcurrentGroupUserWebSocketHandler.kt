package com.hero.alignlab.domain.group.handle

import com.hero.alignlab.common.extension.mapper
import com.hero.alignlab.domain.auth.application.AuthFacade
import com.hero.alignlab.domain.auth.model.AUTH_TOKEN_KEY
import com.hero.alignlab.domain.auth.model.AuthUserToken
import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.NotFoundException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID.randomUUID

class ReactiveConcurrentGroupUserWebSocketHandler(
    private val authFacade: AuthFacade,
) : WebSocketHandler {
    private val logger = KotlinLogging.logger { }

    private val eventFlux: Flux<String> = Flux.generate { sink ->
        val event = Event(randomUUID().toString(), LocalDateTime.now().toString())
        try {
            sink.next(mapper.writeValueAsString(event))
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

        logger.info { "concurrent user ${user.uid}" }

        return session.send(
            Flux.interval(Duration.ofMillis(1000L))
                .zipWith(eventFlux) { _, event -> event }
                .map(session::textMessage)
        ).and(session.receive().map(WebSocketMessage::getPayloadAsText).log()).then()
    }

    private fun isTokenHeader(headerKey: String): Boolean {
        return AUTH_TOKEN_KEY.equals(headerKey, ignoreCase = true)
    }
}

data class Event(val id: String, val timestamp: String)
