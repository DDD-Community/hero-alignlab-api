package com.hero.alignlab.client.kakao.model.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GenerateKakaoOAuthTokenResponse(
    val tokenType: String,
    val accessToken: String,
    val idToken: String?,
    val expiresIn: Int,
    val refreshToken: String,
    val refreshTokenExpiresIn: Int,
    val scope: String?
)
