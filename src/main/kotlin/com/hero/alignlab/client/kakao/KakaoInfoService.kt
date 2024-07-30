package com.hero.alignlab.client.kakao

import com.hero.alignlab.client.kakao.client.KaKaoInfoClient
import com.hero.alignlab.client.kakao.model.response.KakaoOAuthUserInfoResponse
import com.hero.alignlab.common.extension.resolveCancellation
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class KakaoInfoService(
    private val kaKaoInfoClient: KaKaoInfoClient,
) {
    private val logger = KotlinLogging.logger { }

    suspend fun getUserInfo(accessToken: String): KakaoOAuthUserInfoResponse {
        return runCatching {
            kaKaoInfoClient.getUserInfo(accessToken)
        }.onFailure { e ->
            logger.resolveCancellation("getUserInfo", e)
        }.getOrThrow()
    }
}
