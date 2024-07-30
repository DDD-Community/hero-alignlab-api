package com.hero.alignlab.client.kakao.client

import com.hero.alignlab.client.kakao.model.request.GenerateKakaoOAuthTokenRequest
import com.hero.alignlab.client.kakao.model.response.GenerateKakaoOAuthTokenResponse

interface KaKaoOAuthClient {
    suspend fun getOAuthAuthorizeCode(clientId: String, redirectUri: String)

    suspend fun generateOAuthToken(request: GenerateKakaoOAuthTokenRequest): GenerateKakaoOAuthTokenResponse
}
