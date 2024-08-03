package com.hero.alignlab.domain.dev.application

import com.hero.alignlab.common.extension.CoroutineExtension.retryOnError
import com.hero.alignlab.ws.handler.ReactiveConcurrentUserWebSocketHandler
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class DevWebsocketService(
    private val webSocketHandler: ReactiveConcurrentUserWebSocketHandler
) {
    private val logger = KotlinLogging.logger { }

    suspend fun forceCloseAllWebSocketSessions() {
        runCatching {
            withContext(Dispatchers.IO) {
                retryOnError(2) {
                    webSocketHandler.forceCloseAllWebSocketSessions()
                }
            }
        }.onFailure { e ->
            logger.error(e) { "forceCloseAllWebSocketSessions" }
        }.getOrThrow()
    }
}
