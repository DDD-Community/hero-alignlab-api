package com.hero.alignlab.client.kakao.client

import com.hero.alignlab.client.kakao.SuspendableClient
import com.hero.alignlab.client.kakao.config.KakaoOAuthClientConfig
import org.springframework.web.reactive.function.client.WebClient

class SuspendableKakaoOAuthClient(
    client: WebClient,
    private val config: KakaoOAuthClientConfig.Config,
) : KaKaoOAuthClient, SuspendableClient(client) {
    override suspend fun getOAuthAuthorizeCode(redirectUrl: String?) {
        client
            .get()
            .uri("/authorize") { builder ->
                builder
                    .queryParam("response_type", "code")
                    .queryParam("client_id", config.restApiKey)
                    .queryParam("redirect_uri", redirectUrl ?: config.redirectUrl)
                    .build()
            }.requestOrNull<Any>()
    }
}
