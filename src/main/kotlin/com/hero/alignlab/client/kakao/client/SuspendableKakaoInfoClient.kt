package com.hero.alignlab.client.kakao.client

import com.hero.alignlab.client.kakao.SuspendableClient
import com.hero.alignlab.client.kakao.model.response.KakaoOAuthUserInfoResponse
import org.springframework.web.reactive.function.client.WebClient

class SuspendableKakaoInfoClient(client: WebClient) : KaKaoInfoClient, SuspendableClient(client) {
    override suspend fun getUserInfo(accessToken: String): KakaoOAuthUserInfoResponse {
        return client.get()
            .uri("/v2/user/me")
            .header("Authorization", "Bearer $accessToken")
            .request()
    }
}
