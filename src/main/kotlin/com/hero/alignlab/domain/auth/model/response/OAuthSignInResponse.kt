package com.hero.alignlab.domain.auth.model.response

data class OAuthSignInResponse(
    val uid: Long,
    val nickname: String,
    /** hero access token */
    val accessToken: String,
)
