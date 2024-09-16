package com.hero.alignlab.domain.dev.resource

import com.hero.alignlab.config.swagger.SwaggerTag.DEV_TAG
import com.hero.alignlab.domain.auth.model.DevAuthUser
import com.hero.alignlab.domain.user.application.UserInfoService
import com.hero.alignlab.domain.user.model.response.UserInfoResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = DEV_TAG)
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class DevUserInfoResource(
    private val userInfoService: UserInfoService,
) {
    @Operation(summary = "[DEV] 유저 정보 조회")
    @GetMapping("/api/dev/v1/users/{id}")
    suspend fun getUserInfo(
        dev: DevAuthUser,
        @PathVariable id: Long,
    ): UserInfoResponse {
        return userInfoService.getUserInfo(id)
    }
}
