package com.hero.alignlab.domain.user.resource

import com.hero.alignlab.common.extension.wrapOk
import com.hero.alignlab.common.model.Response
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.user.application.UserInfoService
import com.hero.alignlab.domain.user.model.request.ChangeNicknameRequest
import com.hero.alignlab.domain.user.model.response.ChangeNicknameResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "User API")
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class UserResource(
    private val userInfoService: UserInfoService,
) {
    @Operation(summary = "유저 닉네임 변경")
    @PutMapping("/api/v1/users/{id}/nickname")
    suspend fun changeNickname(
        user: AuthUser,
        @PathVariable id: Long,
        @RequestBody request: ChangeNicknameRequest,
    ): ResponseEntity<Response<ChangeNicknameResponse>> {
        return userInfoService.changeNickname(
            user = user,
            id = id,
            request = request
        ).wrapOk()
    }
}
