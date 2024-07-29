package com.hero.alignlab.client.kakao.client

interface KaKaoOAuthClient {
    suspend fun getOAuthAuthorizeCode(redirectUrl: String? = null)
}
