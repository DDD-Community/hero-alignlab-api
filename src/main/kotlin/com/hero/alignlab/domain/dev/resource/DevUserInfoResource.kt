package com.hero.alignlab.domain.dev.resource

import com.hero.alignlab.common.extension.wrapOk
import com.hero.alignlab.config.dev.DevResourceCheckConfig.Companion.devResource
import com.hero.alignlab.config.swagger.SwaggerTag.DEV_TAG
import com.hero.alignlab.domain.user.application.UserInfoService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@Tag(name = DEV_TAG)
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class DevUserInfoResource(
    private val userInfoService: UserInfoService,
) {
    @Operation(summary = "[DEV] 유저 정보 조회")
    @GetMapping("/api/dev/v1/users/{id}")
    suspend fun getUserInfo(
        @PathVariable id: Long,
        @RequestHeader("X-HERO-DEV-TOKEN") token: String
    ) = devResource(token) {
        userInfoService.getUserInfo(id).wrapOk()
    }
}
