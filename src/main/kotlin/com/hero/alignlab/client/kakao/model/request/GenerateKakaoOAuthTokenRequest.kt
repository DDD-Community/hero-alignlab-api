package com.hero.alignlab.client.kakao.model.request

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GenerateKakaoOAuthTokenRequest(
    val clientId: String,
    val redirectUri: String,
    val code: String,
    val clientSecret: String? = null
)
