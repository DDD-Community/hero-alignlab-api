package com.hero.alignlab.domain.health.resource

import com.hero.alignlab.common.model.Response
import com.hero.alignlab.domain.health.model.response.HealthResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.core.env.Environment
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
class HealthRsocket(
    private val environment: Environment
) {
    private val logger = KotlinLogging.logger {}

    @MessageMapping("rs.health")
    suspend fun healthSocket(): Response<HealthResponse> {
        logger.debug { "rsocket health check" }
        return environment.activeProfiles.first()
            .run { HealthResponse.from(this) }
            .run { Response(this) }
    }
}
