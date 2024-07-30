package com.hero.alignlab.domain.auth.resource

import com.hero.alignlab.common.extension.wrapCreated
import com.hero.alignlab.common.extension.wrapOk
import com.hero.alignlab.domain.auth.application.OAuthFacade
import com.hero.alignlab.domain.auth.model.OAuthProvider
import com.hero.alignlab.domain.auth.model.request.OAuthSignInRequest
import com.hero.alignlab.domain.auth.model.request.OAuthSignUpRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
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
    ) = oAuthFacade.checkSignUp(provider, accessToken).wrapOk()

    @Operation(summary = "로그인")
    @PostMapping("/api/v1/oauth/{provider}/sign-in")
    suspend fun signIn(
        @PathVariable provider: OAuthProvider,
        @RequestBody request: OAuthSignInRequest,
    ) = oAuthFacade.signIn(provider, request).wrapOk()

    @Operation(summary = "회원가입")
    @PostMapping("/api/v1/oauth/{provider}/sign-up")
    suspend fun signUp(
        @PathVariable provider: OAuthProvider,
        @RequestBody request: OAuthSignUpRequest,
    ) = oAuthFacade.signUp(provider, request).wrapCreated()
}
