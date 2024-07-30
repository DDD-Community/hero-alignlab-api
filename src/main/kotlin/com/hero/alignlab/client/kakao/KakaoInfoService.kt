package com.hero.alignlab.client.kakao

import com.hero.alignlab.client.kakao.client.KakaoInfoClient
import com.hero.alignlab.client.kakao.model.response.KakaoOAuthUserInfoResponse
import com.hero.alignlab.common.extension.resolveCancellation
import com.hero.alignlab.domain.auth.model.OAuthProvider
import com.hero.alignlab.domain.auth.model.OAuthUserInfoModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class KakaoInfoService(
    private val kaKaoInfoClient: KakaoInfoClient,
) {
    private val logger = KotlinLogging.logger { }

    suspend fun getOAuthInfo(accessToken: String): OAuthUserInfoModel {
        val oauthInfo = getUserInfo(accessToken)

        return OAuthUserInfoModel(
            provider = OAuthProvider.kakao,
            oauthId = oauthInfo.id,
            nickname = oauthInfo.kakaoAccount.profile.nickname,
            profileImageUrl = oauthInfo.kakaoAccount.profile.profileImageUrl
        )
    }

    suspend fun getUserInfo(accessToken: String): KakaoOAuthUserInfoResponse {
        return runCatching {
            withContext(Dispatchers.IO) {
                kaKaoInfoClient.getUserInfo(accessToken)
            }
        }.onFailure { e ->
            logger.resolveCancellation("getUserInfo", e)
        }.getOrThrow()
    }
}
