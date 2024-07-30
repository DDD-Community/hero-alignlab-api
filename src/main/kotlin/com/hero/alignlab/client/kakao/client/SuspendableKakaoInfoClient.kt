package com.hero.alignlab.client.kakao.client

import com.hero.alignlab.client.kakao.SuspendableClient
import com.hero.alignlab.client.kakao.config.KakaoInfoClientConfig
import com.hero.alignlab.client.kakao.model.request.KakaoOAuthUnlinkRequest
import com.hero.alignlab.client.kakao.model.response.KakaoOAuthUnlinkResponse
import com.hero.alignlab.client.kakao.model.response.KakaoOAuthUserInfoResponse
import org.springframework.web.reactive.function.client.WebClient

class SuspendableKakaoInfoClient(
    client: WebClient,
    private val config: KakaoInfoClientConfig.Config,
) : KakaoInfoClient, SuspendableClient(client) {
    override suspend fun getUserInfo(accessToken: String): KakaoOAuthUserInfoResponse {
        return client.get()
            .uri("/v2/user/me")
            .header("Authorization", "Bearer $accessToken")
            .request()
    }

    override suspend fun unlink(accessToken: String, request: KakaoOAuthUnlinkRequest): KakaoOAuthUnlinkResponse {
        return client.post()
            .uri(config.unlinkPath)
            .bodyValue(request)
            .header("Authorization", "Bearer $accessToken")
            .request()
    }
}
