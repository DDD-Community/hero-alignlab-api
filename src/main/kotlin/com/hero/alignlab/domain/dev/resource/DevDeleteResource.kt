package com.hero.alignlab.domain.dev.resource

import com.hero.alignlab.config.swagger.SwaggerTag.DEV_TAG
import com.hero.alignlab.domain.auth.model.DevAuthUser
import com.hero.alignlab.domain.dev.application.DevDeleteService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = DEV_TAG)
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class DevDeleteResource(
    private val devDeleteService: DevDeleteService
) {
    @Operation(summary = "[DEV] 전체 데이터 삭제, block 시간이 오래걸림.")
    @DeleteMapping("/api/dev/v1/users/{uid}")
    fun deleteUserData(
        dev: DevAuthUser,
        @PathVariable uid: Long,
    ) {
        devDeleteService.deleteAll(uid)
    }
}
