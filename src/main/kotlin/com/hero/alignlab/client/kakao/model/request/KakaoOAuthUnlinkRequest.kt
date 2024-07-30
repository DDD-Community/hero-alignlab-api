package com.hero.alignlab.client.kakao.model.request

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class KakaoOAuthUnlinkRequest(
    val targetIdType: String = "user_id",
    val targetId: String,
)
