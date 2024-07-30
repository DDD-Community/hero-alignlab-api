package com.hero.alignlab.client.kakao

import com.hero.alignlab.client.kakao.client.KaKaoOAuthClient
import com.hero.alignlab.client.kakao.config.KakaoOAuthClientConfig
import com.hero.alignlab.client.kakao.model.request.GenerateKakaoOAuthTokenRequest
import com.hero.alignlab.client.kakao.model.response.GenerateKakaoOAuthTokenResponse
import com.hero.alignlab.common.extension.resolveCancellation
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class KakaoOAuthService(
    private val kaKaoOAuthClient: KaKaoOAuthClient,
    private val config: KakaoOAuthClientConfig.Config
) {
    private val logger = KotlinLogging.logger {}

    suspend fun getOAuthLoginLinkDev(): String {
        return config.url + String.format(config.authorizedUrl, config.restApiKey, config.redirectUrl)
    }

    suspend fun getOAuthAuthorizeCode() {
        getOAuthAuthorizeCode(config.restApiKey, config.redirectUrl)
    }

    suspend fun getOAuthAuthorizeCode(clientId: String, redirectUri: String) {
        runCatching {
            withContext(Dispatchers.IO) {
                kaKaoOAuthClient.getOAuthAuthorizeCode(clientId, redirectUri)
            }
        }.onFailure { e ->
            logger.resolveCancellation("getOAuthAuthorizeCode", e)
        }.getOrThrow()
    }

    suspend fun generateOAuthToken(code: String): GenerateKakaoOAuthTokenResponse {
        val request = GenerateKakaoOAuthTokenRequest(
            clientId = config.restApiKey,
            redirectUri = config.redirectUrl,
            code = code,
            clientSecret = config.clientSecretCode
        )

        return generateOAuthToken(request)
    }

    suspend fun generateOAuthToken(request: GenerateKakaoOAuthTokenRequest): GenerateKakaoOAuthTokenResponse {
        return runCatching {
            withContext(Dispatchers.IO) {
                kaKaoOAuthClient.generateOAuthToken(request)
            }
        }.onFailure { e ->
            logger.resolveCancellation("generateOAuthToken", e)
        }.getOrThrow()
    }
}
