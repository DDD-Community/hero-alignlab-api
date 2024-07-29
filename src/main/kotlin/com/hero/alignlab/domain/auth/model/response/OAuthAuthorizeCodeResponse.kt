package com.hero.alignlab.domain.auth.model.response

import com.hero.alignlab.domain.auth.model.OAuthProvider

data class OAuthAuthorizeCodeResponse(
    val provider: OAuthProvider,
)
