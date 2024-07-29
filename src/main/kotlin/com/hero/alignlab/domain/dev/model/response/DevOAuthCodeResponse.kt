package com.hero.alignlab.domain.dev.model.response

import com.hero.alignlab.domain.auth.model.OAuthProvider

data class DevOAuthCodeResponse(
    val provider: OAuthProvider,
    val authorizedUrl: String,
)
