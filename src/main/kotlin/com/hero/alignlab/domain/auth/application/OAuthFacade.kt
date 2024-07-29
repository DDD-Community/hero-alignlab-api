package com.hero.alignlab.domain.auth.application

import com.hero.alignlab.client.kakao.client.KaKaoOAuthClient
import com.hero.alignlab.domain.auth.model.OAuthProvider
import com.hero.alignlab.domain.auth.model.response.OAuthAuthorizeCodeResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class OAuthFacade(
    private val kaKaoOAuthClient: KaKaoOAuthClient,
) {
    private val logger = KotlinLogging.logger { }

    suspend fun getOAuthAuthorizeCode(provider: OAuthProvider): OAuthAuthorizeCodeResponse {
        when (provider) {
            OAuthProvider.kakao -> kaKaoOAuthClient.getOAuthAuthorizeCode()
        }

        return OAuthAuthorizeCodeResponse(provider)
    }
}
