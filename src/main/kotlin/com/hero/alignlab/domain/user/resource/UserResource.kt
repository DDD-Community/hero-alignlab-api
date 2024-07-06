package com.hero.alignlab.domain.user.resource

import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.user.application.UserService
import com.hero.alignlab.extension.wrapOk
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "사용자 관리")
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class UserResource(
    private val userService: UserService,
) {
    @Operation(summary = "토큰 기반으로 유저 정보를 조회")
    @GetMapping("/api/v1/users/me")
    suspend fun getUserInfo(
        user: AuthUser
    ) = userService.getUserInfo(user).wrapOk()
}
