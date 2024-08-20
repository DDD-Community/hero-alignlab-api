package com.hero.alignlab.domain.auth.application

import com.hero.alignlab.client.kakao.KakaoInfoService
import com.hero.alignlab.client.kakao.model.request.KakaoOAuthUnlinkRequest
import com.hero.alignlab.common.extension.executes
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.auth.model.OAuthProvider
import com.hero.alignlab.domain.auth.model.request.OAuthSignInRequest
import com.hero.alignlab.domain.auth.model.request.OAuthSignUpRequest
import com.hero.alignlab.domain.auth.model.response.OAuthCheckSignUpResponse
import com.hero.alignlab.domain.auth.model.response.OAuthSignInResponse
import com.hero.alignlab.domain.auth.model.response.OAuthSignUpResponse
import com.hero.alignlab.domain.user.application.OAuthUserInfoService
import com.hero.alignlab.domain.user.application.UserInfoService
import com.hero.alignlab.domain.user.domain.OAuthUserInfo
import com.hero.alignlab.domain.user.domain.UserInfo
import com.hero.alignlab.event.model.WithdrawEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class OAuthFacade(
    private val oAuthService: OAuthService,
    private val oAuthUserInfoService: OAuthUserInfoService,
    private val userInfoService: UserInfoService,
    private val jwtTokenService: JwtTokenService,
    private val txTemplates: TransactionTemplates,
    private val kakaoInfoService: KakaoInfoService,
    private val publisher: ApplicationEventPublisher,
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

    suspend fun signIn(provider: OAuthProvider, request: OAuthSignInRequest): OAuthSignInResponse? {
        val oauthInfo = oAuthService.getOAuthInfo(provider, request.accessToken)

        val userInfo = userInfoService.findByOAuthOrThrow(provider.toProvider(), oauthInfo.oauthId)

        val token = jwtTokenService.createToken(userInfo.id, TOKEN_EXPIRED_DATE)

        return OAuthSignInResponse(
            uid = userInfo.id,
            nickname = userInfo.nickname,
            accessToken = token
        )
    }

    suspend fun signUp(provider: OAuthProvider, request: OAuthSignUpRequest): OAuthSignUpResponse {
        val oauthInfo = oAuthService.getOAuthInfo(provider, request.accessToken)

        val isExists = oAuthUserInfoService.existsByOauthIdAndProvider(oauthInfo.oauthId, provider.toProvider())

        val userInfo = when (isExists) {
            true -> userInfoService.findByOAuthOrThrow(provider.toProvider(), oauthInfo.oauthId)
            false -> {
                txTemplates.writer.executes {
                    val createdUser = userInfoService.saveSync(UserInfo(nickname = oauthInfo.nickname))

                    oAuthUserInfoService.saveSync(
                        OAuthUserInfo(
                            uid = createdUser.id,
                            provider = provider.toProvider(),
                            oauthId = oauthInfo.oauthId
                        )
                    )

                    createdUser
                }
            }
        }

        val token = jwtTokenService.createToken(userInfo.id, TOKEN_EXPIRED_DATE)

        return OAuthSignUpResponse(
            uid = userInfo.id,
            nickname = userInfo.nickname,
            accessToken = token
        )
    }

    suspend fun withdraw(
        provider: OAuthProvider,
        accessToken: String,
        oauthId: String
    ) {
        when (provider) {
            OAuthProvider.kakao -> kakaoInfoService.unlink(accessToken, KakaoOAuthUnlinkRequest(targetId = oauthId))
        }

        val oauthUser = oAuthUserInfoService.findByProviderAndOauthId(provider.toProvider(), oauthId)

        if (oauthUser != null) {
            /** 유저 정보는 즉시 삭제 */
            txTemplates.writer.executes {
                oAuthUserInfoService.deleteSync(provider.toProvider(), oauthId)
                userInfoService.deleteBySync(oauthUser.uid)

                publisher.publishEvent(WithdrawEvent(oauthUser.uid))
            }
        }
    }
}
