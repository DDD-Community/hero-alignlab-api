package com.hero.alignlab.domain.dev.application

import com.hero.alignlab.client.kakao.KakaoOAuthService
import com.hero.alignlab.client.kakao.config.KakaoOAuthClientConfig
import com.hero.alignlab.client.kakao.model.response.GenerateKakaoOAuthTokenResponse
import com.hero.alignlab.domain.auth.model.OAuthProvider
import com.hero.alignlab.domain.auth.model.request.OAuthAuthorizedRequest
import com.hero.alignlab.domain.dev.model.request.DevRedirectedRequest
import com.hero.alignlab.domain.dev.model.response.DevOAuthCodeResponse
import com.hero.alignlab.domain.dev.model.response.DevRedirectedResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class DevOAuthService(
    private val kakaoOAuthService: KakaoOAuthService,
    private val config: KakaoOAuthClientConfig.Config,
) {
    private val logger = KotlinLogging.logger { }

    suspend fun getOAuthAuthorizeCode(provider: OAuthProvider): DevOAuthCodeResponse {
        when (provider) {
            OAuthProvider.kakao -> kakaoOAuthService.getOAuthAuthorizeCode(config.devRedirectUrl)
        }

        return DevOAuthCodeResponse(provider)
    }

    fun redirectTest(
        provider: OAuthProvider,
        request: DevRedirectedRequest
    ): DevRedirectedResponse {
        logger.info { "redirect test provider: $provider request: $request" }

        return DevRedirectedResponse(
            provider = provider,
            requestParams = request,
        )
    }

    suspend fun resolveOAuth(
        provider: OAuthProvider,
        request: OAuthAuthorizedRequest
    ): GenerateKakaoOAuthTokenResponse {
        return when (provider) {
            OAuthProvider.kakao -> kakaoOAuthService.generateOAuthToken(request.code, config.devRedirectUrl)
        }
    }
}
