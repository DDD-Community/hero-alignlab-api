package com.hero.alignlab.domain.dev.application

import com.hero.alignlab.client.kakao.KakaoInfoService
import com.hero.alignlab.client.kakao.KakaoOAuthService
import com.hero.alignlab.client.kakao.model.request.KakaoOAuthUnlinkRequest
import com.hero.alignlab.client.kakao.model.response.GenerateKakaoOAuthTokenResponse
import com.hero.alignlab.client.kakao.model.response.KakaoOAuthUserInfoResponse
import com.hero.alignlab.common.extension.executes
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.auth.model.OAuthProvider
import com.hero.alignlab.domain.dev.model.response.DevOAuthCodeResponse
import com.hero.alignlab.domain.user.application.OAuthUserInfoService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class DevOAuthService(
    private val kakaoOAuthService: KakaoOAuthService,
    private val kakaoInfoService: KakaoInfoService,
    private val oAuthUserInfoService: OAuthUserInfoService,
    private val txTemplates: TransactionTemplates,
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

    suspend fun getUserInfo(accessToken: String): KakaoOAuthUserInfoResponse {
        return kakaoInfoService.getUserInfo(accessToken)
    }

    suspend fun withdraw(provider: OAuthProvider, accessToken: String, oauthId: String): Boolean {
        when (provider) {
            OAuthProvider.kakao -> kakaoInfoService.unlink(accessToken, KakaoOAuthUnlinkRequest(targetId = oauthId))
        }

        if (oAuthUserInfoService.existsByOauthIdAndProvider(oauthId, provider.toProvider())) {
            txTemplates.writer.executes {
                oAuthUserInfoService.deleteSync(provider.toProvider(), oauthId)
            }

            return true
        }

        return false
    }
}
