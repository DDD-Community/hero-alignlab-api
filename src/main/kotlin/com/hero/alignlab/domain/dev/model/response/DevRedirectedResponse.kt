package com.hero.alignlab.domain.dev.model.response

import com.hero.alignlab.domain.auth.model.OAuthProvider
import com.hero.alignlab.domain.dev.model.request.DevRedirectedRequest

data class DevRedirectedResponse(
    val provider: OAuthProvider,
    val requestParams: DevRedirectedRequest,
)
