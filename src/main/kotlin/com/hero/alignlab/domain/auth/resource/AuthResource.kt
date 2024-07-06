package com.hero.alignlab.domain.auth.resource

import com.hero.alignlab.domain.auth.application.AuthFacade
import com.hero.alignlab.domain.auth.model.request.SignInRequest
import com.hero.alignlab.domain.auth.model.request.SignUpRequest
import com.hero.alignlab.extension.wrapCreated
import com.hero.alignlab.extension.wrapOk
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Auth 인증 및 인가 관리")
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class AuthResource(
    private val authFacade: AuthFacade,
) {
    @Operation(summary = "회원가입")
    @PostMapping("/api/v1/auth/sign-up")
    suspend fun signUp(
        @RequestBody request: SignUpRequest,
    ) = authFacade.signUp(request).wrapCreated()

    @Operation(summary = "로그인")
    @PostMapping("/api/v1/auth/sign-in")
    suspend fun signUp(
        @RequestBody request: SignInRequest,
    ) = authFacade.signIn(request).wrapOk()
}
