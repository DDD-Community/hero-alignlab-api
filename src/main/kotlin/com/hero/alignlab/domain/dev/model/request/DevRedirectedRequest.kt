package com.hero.alignlab.domain.dev.model.request

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class DevRedirectedRequest(
    val code: String?,
    val error: String?,
    val errorDescription: String?,
    val state: String?,
)
