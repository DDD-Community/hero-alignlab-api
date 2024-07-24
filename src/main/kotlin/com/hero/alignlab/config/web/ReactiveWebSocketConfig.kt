package com.hero.alignlab.config.web

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy

@Configuration
class ReactiveWebSocketConfiguration {
    @Bean
    fun reactiveWebSocketHandlerMapping(
        @Qualifier("reactiveWebSocketHandler") reactiveWebSocketHandler: WebSocketHandler
    ): HandlerMapping {
        val map = mapOf(
            /** 접속 유저 처리 */
            "/ws/v1/groups/concurrent-users" to reactiveWebSocketHandler
        )

        return SimpleUrlHandlerMapping().apply {
            this.order = 1
            this.urlMap = map
        }
    }

    @Bean
    fun handlerAdapter(): WebSocketHandlerAdapter {
        return WebSocketHandlerAdapter(webSocketService())
    }

    @Bean
    fun webSocketService(): HandshakeWebSocketService {
        return HandshakeWebSocketService(ReactorNettyRequestUpgradeStrategy())
    }

    @Bean
    fun reactiveWebSocketHandler(): WebSocketHandler {
        return ReactiveWebSocketHandler()
    }
}
