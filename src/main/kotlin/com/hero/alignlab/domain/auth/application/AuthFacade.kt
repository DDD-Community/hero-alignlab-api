package com.hero.alignlab.domain.auth.application

import arrow.fx.coroutines.parZip
import com.hero.alignlab.common.encrypt.EncryptData
import com.hero.alignlab.common.encrypt.Encryptor
import com.hero.alignlab.common.extension.coExecute
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.auth.model.AuthUserImpl
import com.hero.alignlab.domain.auth.model.AuthUserToken
import com.hero.alignlab.domain.auth.model.request.SignInRequest
import com.hero.alignlab.domain.auth.model.request.SignUpRequest
import com.hero.alignlab.domain.auth.model.response.SignInResponse
import com.hero.alignlab.domain.auth.model.response.SignUpResponse
import com.hero.alignlab.domain.user.application.CredentialUserInfoService
import com.hero.alignlab.domain.user.application.OAuthUserInfoService
import com.hero.alignlab.domain.user.application.UserInfoService
import com.hero.alignlab.domain.user.domain.CredentialUserInfo
import com.hero.alignlab.domain.user.domain.UserInfo
import com.hero.alignlab.domain.user.domain.vo.OAuthProvider
import com.hero.alignlab.domain.user.model.response.AuthProvider
import com.hero.alignlab.domain.user.model.response.UserInfoResponse
import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.InvalidRequestException
import com.hero.alignlab.exception.InvalidTokenException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class AuthFacade(
    private val userInfoService: UserInfoService,
    private val credentialUserInfoService: CredentialUserInfoService,
    private val oAuthUserInfoService: OAuthUserInfoService,
    private val jwtTokenService: JwtTokenService,
    private val encryptor: Encryptor,
    private val txTemplates: TransactionTemplates,
) {
    companion object {
        private val TOKEN_EXPIRED_DATE = LocalDateTime.of(2025, 12, 29, 0, 0, 0)
    }

    suspend fun resolveAuthUser(token: AuthUserToken): AuthUser {
        val payload = jwtTokenService.verifyToken(token)

        if (payload.type != "accessToken") {
            throw InvalidTokenException(ErrorCode.INVALID_ACCESS_TOKEN)
        }

        val user = userInfoService.getUserByIdOrThrow(payload.id)

        return AuthUserImpl.from(user)
    }

    fun resolveAuthUser(token: Mono<AuthUserToken>): Mono<Any> {
        return jwtTokenService.verifyTokenMono(token)
            .handle { payload, sink ->
                if (payload.type != "accessToken") {
                    sink.error(InvalidTokenException(ErrorCode.INVALID_ACCESS_TOKEN))
                    return@handle
                }

                val user = userInfoService.getUserByIdOrThrowSync(payload.id)

                sink.next(AuthUserImpl.from(user))
            }
    }

    suspend fun signUp(request: SignUpRequest): SignUpResponse {
        if (credentialUserInfoService.existsByUsername(request.username)) {
            throw InvalidRequestException(ErrorCode.DUPLICATED_USERNAME_ERROR)
        }

        val userInfo = txTemplates.writer.coExecute {
            val userInfo = userInfoService.saveSync(UserInfo(nickname = request.username))

            credentialUserInfoService.saveSync(
                CredentialUserInfo(
                    uid = userInfo.id,
                    username = request.username,
                    password = EncryptData.enc(request.password, encryptor)
                )
            )

            userInfo
        }

        val accessToken = jwtTokenService.createToken(userInfo.id, TOKEN_EXPIRED_DATE)

        return SignUpResponse(accessToken)
    }

    suspend fun signIn(request: SignInRequest): SignInResponse {
        val userInfo = userInfoService.findByCredentialOrThrow(request.username, request.password)

        val accessToken = jwtTokenService.createToken(userInfo.id, TOKEN_EXPIRED_DATE)

        return SignInResponse(accessToken)
    }

    suspend fun getUserInfo(user: AuthUser): UserInfoResponse {
        return parZip(
            { userInfoService.getUserByIdOrThrow(user.uid) },
            { credentialUserInfoService.findAllByUid(user.uid) },
            { oAuthUserInfoService.findAllByUid(user.uid) },
        ) { userInfo, credentialUsers, oAuthUsers ->
            val providers = buildList {
                if (credentialUsers.isNotEmpty()) {
                    add(AuthProvider.BASIC)
                }

                repeat(
                    oAuthUsers.filter { it.provider == OAuthProvider.KAKAO }.size
                ) { add(AuthProvider.KAKAO) }
            }

            UserInfoResponse.of(userInfo, providers)
        }
    }
}
