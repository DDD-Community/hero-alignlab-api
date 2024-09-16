package com.hero.alignlab.domain.auth.resource

import com.hero.alignlab.common.extension.wrapCreated
import com.hero.alignlab.common.extension.wrapOk
import com.hero.alignlab.common.extension.wrapVoid
import com.hero.alignlab.common.model.Response
import com.hero.alignlab.domain.auth.application.OAuthFacade
import com.hero.alignlab.domain.auth.model.OAuthProvider
import com.hero.alignlab.domain.auth.model.request.OAuthSignInRequest
import com.hero.alignlab.domain.auth.model.request.OAuthSignUpRequest
import com.hero.alignlab.domain.auth.model.response.OAuthCheckSignUpResponse
import com.hero.alignlab.domain.auth.model.response.OAuthSignInResponse
import com.hero.alignlab.domain.auth.model.response.OAuthSignUpResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * - [Kakao Rest Auth](https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api)
 */
@Tag(name = "OAuth 인증 및 인가 관리")
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class OAuthResource(
    private val oAuthFacade: OAuthFacade,
) {
    @Operation(summary = "회원가입 여부 확인")
    @GetMapping("/api/v1/oauth/{provider}/sign-up/check")
    suspend fun checkSignUp(
        @PathVariable provider: OAuthProvider,
        @RequestParam accessToken: String,
    ): ResponseEntity<Response<OAuthCheckSignUpResponse>> {
        return oAuthFacade.checkSignUp(provider, accessToken).wrapOk()
    }

    @Operation(summary = "로그인")
    @PostMapping("/api/v1/oauth/{provider}/sign-in")
    suspend fun signIn(
        @PathVariable provider: OAuthProvider,
        @RequestBody request: OAuthSignInRequest,
    ): ResponseEntity<Response<OAuthSignInResponse?>> {
        return oAuthFacade.signIn(provider, request).wrapOk()
    }

    @Operation(summary = "회원가입")
    @PostMapping("/api/v1/oauth/{provider}/sign-up")
    suspend fun signUp(
        @PathVariable provider: OAuthProvider,
        @RequestBody request: OAuthSignUpRequest,
    ): ResponseEntity<Response<OAuthSignUpResponse>> {
        return oAuthFacade.signUp(provider, request).wrapCreated()
    }

    @Operation(summary = "탈퇴하기")
    @DeleteMapping("/api/v1/oauth/{provider}/withdraw")
    suspend fun withdraw(
        @PathVariable provider: OAuthProvider,
        @RequestParam accessToken: String,
        @RequestParam oauthId: String,
    ): ResponseEntity<Unit> {
        return oAuthFacade.withdraw(
            provider = provider,
            accessToken = accessToken,
            oauthId = oauthId
        ).wrapVoid()
    }
}
