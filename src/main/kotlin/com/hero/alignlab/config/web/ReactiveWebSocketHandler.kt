package com.hero.alignlab.config.web

import com.hero.alignlab.common.extension.mapper
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID.randomUUID

class ReactiveWebSocketHandler : WebSocketHandler {
    private val eventFlux: Flux<String> = Flux.generate { sink ->
        val event = Event(randomUUID().toString(), LocalDateTime.now().toString())
        try {
            sink.next(mapper.writeValueAsString(event))
        } catch (e: Exception) {
            sink.error(e)
        }
    }

    override fun handle(session: WebSocketSession): Mono<Void> {
        return session.send(
            Flux.interval(Duration.ofMillis(1000L))
                .zipWith(eventFlux) { _, event -> event }
                .map(session::textMessage)
        ).and(session.receive().map(WebSocketMessage::getPayloadAsText).log()).then()
    }
}

data class Event(val id: String, val timestamp: String)
