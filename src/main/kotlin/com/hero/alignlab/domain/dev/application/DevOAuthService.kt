package com.hero.alignlab.domain.dev.application

import com.hero.alignlab.client.kakao.client.KaKaoOAuthClient
import com.hero.alignlab.client.kakao.config.KakaoOAuthClientConfig
import com.hero.alignlab.domain.auth.model.OAuthProvider
import com.hero.alignlab.domain.dev.model.response.DevOAuthCodeResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class DevOAuthService(
    private val kaKaoOAuthClient: KaKaoOAuthClient,
    private val config: KakaoOAuthClientConfig.Config,
) {
    private val logger = KotlinLogging.logger { }

    suspend fun getOAuthAuthorizeCode(provider: OAuthProvider): DevOAuthCodeResponse {
        when (provider) {
            OAuthProvider.kakao -> kaKaoOAuthClient.getOAuthAuthorizeCode(config.devRedirectUrl)
        }

        return DevOAuthCodeResponse(provider)
    }
}
