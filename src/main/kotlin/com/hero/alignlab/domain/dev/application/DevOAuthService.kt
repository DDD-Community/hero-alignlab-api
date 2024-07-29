package com.hero.alignlab.domain.dev.application

import com.hero.alignlab.client.kakao.KakaoOAuthService
import com.hero.alignlab.client.kakao.model.response.GenerateKakaoOAuthTokenResponse
import com.hero.alignlab.domain.auth.model.OAuthProvider
import com.hero.alignlab.domain.dev.model.response.DevOAuthCodeResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class DevOAuthService(
    private val kakaoOAuthService: KakaoOAuthService,
) {
    private val logger = KotlinLogging.logger { }

    suspend fun getOAuthAuthorizeCode(provider: OAuthProvider): DevOAuthCodeResponse {
        val url = when (provider) {
            OAuthProvider.kakao -> kakaoOAuthService.getOAuthLoginLinkDev()
        }

        return DevOAuthCodeResponse(provider, url)
    }

    suspend fun resolveOAuth(provider: OAuthProvider, code: String): GenerateKakaoOAuthTokenResponse {
        return when (provider) {
            OAuthProvider.kakao -> kakaoOAuthService.generateOAuthToken(code)
        }
    }
}
