package com.hero.alignlab.client.kakao.client

import com.hero.alignlab.client.kakao.model.response.KakaoOAuthUserInfoResponse

interface KaKaoInfoClient {
    suspend fun getUserInfo(accessToken: String): KakaoOAuthUserInfoResponse
}
