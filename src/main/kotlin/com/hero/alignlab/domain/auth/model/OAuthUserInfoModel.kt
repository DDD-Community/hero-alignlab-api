package com.hero.alignlab.domain.auth.model

data class OAuthUserInfoModel(
    val provider: OAuthProvider,
    val oauthId: String,
    val nickname: String,
    val profileImageUrl: String?
)
