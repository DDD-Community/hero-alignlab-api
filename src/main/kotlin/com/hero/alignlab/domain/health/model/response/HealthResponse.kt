package com.hero.alignlab.domain.health.model.response

import java.time.LocalDateTime

data class HealthResponse(
    val env: String,
    val dateTime: LocalDateTime = LocalDateTime.now(),
    val message: String = "Health Good!"
) {
    companion object {
        fun from(env: String): HealthResponse {
            return HealthResponse(env = env)
        }
    }
}
