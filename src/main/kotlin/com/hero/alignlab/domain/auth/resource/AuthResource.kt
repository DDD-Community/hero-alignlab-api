package com.hero.alignlab.domain.auth.resource

import com.hero.alignlab.common.extension.wrapCreated
import com.hero.alignlab.common.extension.wrapOk
import com.hero.alignlab.common.model.Response
import com.hero.alignlab.domain.auth.application.AuthFacade
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.auth.model.DevAuthUser
import com.hero.alignlab.domain.auth.model.request.SignInRequest
import com.hero.alignlab.domain.auth.model.request.SignUpRequest
import com.hero.alignlab.domain.auth.model.response.SignInResponse
import com.hero.alignlab.domain.auth.model.response.SignUpResponse
import com.hero.alignlab.domain.user.model.response.UserInfoResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Auth 인증 및 인가 관리")
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class AuthResource(
    private val authFacade: AuthFacade,
) {
    /** 일반 회원가입 */
    @Operation(summary = "회원가입")
    @PostMapping("/api/v1/auth/sign-up")
    suspend fun signUp(
        dev: DevAuthUser,
        @RequestBody request: SignUpRequest,
    ): ResponseEntity<Response<SignUpResponse>> {
        return authFacade.signUp(request).wrapCreated()
    }

    /** 일반 로그인 */
    @Operation(summary = "로그인")
    @PostMapping("/api/v1/auth/sign-in")
    suspend fun signUp(
        dev: DevAuthUser,
        @RequestBody request: SignInRequest,
    ): ResponseEntity<Response<SignInResponse>> {
        return authFacade.signIn(request).wrapOk()
    }

    @Operation(summary = "토큰 기반으로 유저 정보를 조회")
    @GetMapping("/api/v1/auth/me")
    suspend fun getUserInfo(
        user: AuthUser
    ): ResponseEntity<Response<UserInfoResponse>> {
        return authFacade.getUserInfo(user).wrapOk()
    }
}
