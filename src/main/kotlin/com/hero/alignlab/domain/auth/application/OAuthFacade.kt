package com.hero.alignlab.domain.auth.application

import com.hero.alignlab.client.kakao.KakaoOAuthService
import com.hero.alignlab.common.extension.toJson
import com.hero.alignlab.domain.auth.model.OAuthProvider
import com.hero.alignlab.domain.auth.model.request.OAuthAuthorizedRequest
import com.hero.alignlab.domain.auth.model.response.OAuthAuthorizeCodeResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class OAuthFacade(
    private val kakaoOAuthService: KakaoOAuthService,
) {
    private val logger = KotlinLogging.logger { }

    suspend fun getOAuthAuthorizeCode(provider: OAuthProvider): OAuthAuthorizeCodeResponse {
        when (provider) {
            OAuthProvider.kakao -> kakaoOAuthService.getOAuthAuthorizeCode()
        }

        return OAuthAuthorizeCodeResponse(provider)
    }

    suspend fun resolveOAuth(provider: OAuthProvider, request: OAuthAuthorizedRequest) {
        val response = when (provider) {
            OAuthProvider.kakao -> kakaoOAuthService.generateOAuthToken(request.code)
        }

        println("response > " + response.toJson())
    }
}
