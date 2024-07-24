package com.hero.alignlab.config.web

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy

@Configuration
class ReactiveWebSocketConfiguration {
    @Bean
    fun reactiveWebSocketHandlerMapping(
        handler: ReactiveConcurrentUserWebSocketHandler
    ): HandlerMapping {
        val map = mapOf(
            /** 접속 유저 처리, 향후 v2로 업데이트 진행 필요 (v1은 poc 개념) */
            "/ws/v1/groups/concurrent-users" to handler
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
}
