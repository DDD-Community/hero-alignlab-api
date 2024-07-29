package com.hero.alignlab.client.kakao.client

import com.hero.alignlab.client.kakao.model.request.GenerateKakaoOAuthTokenRequest
import com.hero.alignlab.client.kakao.model.response.GenerateKakaoOAuthTokenResponse

interface KaKaoOAuthClient {
    suspend fun getOAuthAuthorizeCode(redirectUrl: String? = null)

    suspend fun generateOAuthToken(request: GenerateKakaoOAuthTokenRequest): GenerateKakaoOAuthTokenResponse
}
