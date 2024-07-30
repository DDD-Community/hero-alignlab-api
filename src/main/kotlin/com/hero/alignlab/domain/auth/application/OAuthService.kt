package com.hero.alignlab.domain.auth.application

import com.hero.alignlab.client.kakao.KakaoInfoService
import com.hero.alignlab.domain.auth.model.OAuthProvider
import com.hero.alignlab.domain.auth.model.OAuthUserInfoModel
import org.springframework.stereotype.Service

@Service
class OAuthService(
    private val kakaoInfoService: KakaoInfoService,
) {
    suspend fun getOAuthInfo(provider: OAuthProvider, accessToken: String): OAuthUserInfoModel {
        return when (provider) {
            OAuthProvider.kakao -> kakaoInfoService.getOAuthInfo(accessToken)
        }
    }
}
