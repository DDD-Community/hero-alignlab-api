package com.hero.alignlab.domain.health.resource

import com.hero.alignlab.domain.health.model.response.HealthResponse
import com.hero.alignlab.common.extension.wrapOk
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.core.env.Environment
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "health check")
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class HealthResource(
    private val environment: Environment
) {
    @GetMapping("/api/v1/health")
    suspend fun healthCheckV1() = environment.activeProfiles.first()
        .run { HealthResponse.from(this) }
        .wrapOk()
}
