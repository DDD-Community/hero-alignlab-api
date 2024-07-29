package com.hero.alignlab.client.kakao.client

import com.hero.alignlab.client.kakao.SuspendableClient
import com.hero.alignlab.client.kakao.config.KakaoOAuthClientConfig
import com.hero.alignlab.client.kakao.model.request.GenerateKakaoOAuthTokenRequest
import com.hero.alignlab.client.kakao.model.response.GenerateKakaoOAuthTokenResponse
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

    override suspend fun generateOAuthToken(request: GenerateKakaoOAuthTokenRequest): GenerateKakaoOAuthTokenResponse {
        return client
            .post()
            .uri("/token") { builder ->
                builder
                    .queryParam("grant_type", request.grantType)
                    .queryParam("client_id", request.clientId)
                    .queryParam("redirect_uri", request.redirectUri)
                    .queryParam("code", request.code)
                    .queryParam("client_secret", request.clientSecret)
                    .build()
            }
            .request()
    }
}
