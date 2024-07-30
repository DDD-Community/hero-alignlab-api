package com.hero.alignlab.client.kakao.client

import com.hero.alignlab.client.kakao.model.request.KakaoOAuthUnlinkRequest
import com.hero.alignlab.client.kakao.model.response.KakaoOAuthUnlinkResponse
import com.hero.alignlab.client.kakao.model.response.KakaoOAuthUserInfoResponse

interface KakaoInfoClient {
    suspend fun getUserInfo(accessToken: String): KakaoOAuthUserInfoResponse

    suspend fun unlink(accessToken: String, request: KakaoOAuthUnlinkRequest): KakaoOAuthUnlinkResponse
}
