package com.hero.alignlab.domain.auth.application

import com.hero.alignlab.domain.auth.model.OAuthProvider
import com.hero.alignlab.domain.auth.model.request.OAuthLoginRequest
import com.hero.alignlab.domain.auth.model.response.OAuthCheckSignUpResponse
import com.hero.alignlab.domain.auth.model.response.OAuthLoginResponse
import com.hero.alignlab.domain.user.application.OAuthUserInfoService
import com.hero.alignlab.domain.user.application.UserInfoService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class OAuthFacade(
    private val oAuthService: OAuthService,
    private val oAuthUserInfoService: OAuthUserInfoService,
    private val userInfoService: UserInfoService,
    private val jwtTokenService: JwtTokenService,
) {
    companion object {
        private val TOKEN_EXPIRED_DATE = LocalDateTime.of(2024, 12, 29, 0, 0, 0)
    }

    private val logger = KotlinLogging.logger { }

    suspend fun checkSignUp(provider: OAuthProvider, accessToken: String): OAuthCheckSignUpResponse {
        val oauthInfo = oAuthService.getOAuthInfo(provider, accessToken)

        val isExists = oAuthUserInfoService.existsByOauthIdAndProvider(oauthInfo.oauthId, provider.toProvider())

        return OAuthCheckSignUpResponse(isExists)
    }

    suspend fun signIn(provider: OAuthProvider, request: OAuthLoginRequest): OAuthLoginResponse? {
        val oauthInfo = oAuthService.getOAuthInfo(provider, request.accessToken)

        val userInfo = userInfoService.findByOAuthOrThrow(provider.toProvider(), oauthInfo.oauthId)

        val token = jwtTokenService.createToken(userInfo.id, TOKEN_EXPIRED_DATE)

        return OAuthLoginResponse(
            uid = userInfo.id,
            nickname = userInfo.nickname,
            accessToken = token
        )
    }
}
