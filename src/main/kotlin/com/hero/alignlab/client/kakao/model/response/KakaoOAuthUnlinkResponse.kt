package com.hero.alignlab.client.kakao.model.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class KakaoOAuthUnlinkResponse(
    /** 카카오 회원 탈퇴 유저 id */
    val id: String,
)
