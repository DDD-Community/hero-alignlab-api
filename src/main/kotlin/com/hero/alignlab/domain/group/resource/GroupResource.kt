package com.hero.alignlab.domain.group.resource

import com.hero.alignlab.common.extension.wrapCreated
import com.hero.alignlab.common.extension.wrapOk
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.group.application.GroupService
import com.hero.alignlab.domain.group.model.request.CreateGroupRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@Tag(name = "Group API")
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class GroupResource(
    private val groupService: GroupService,
) {
    @Operation(summary = "그룹 생성")
    @PostMapping("/api/v1/groups")
    suspend fun createGroup(
        user: AuthUser,
        @RequestBody request: CreateGroupRequest
    ) = groupService.createGroup(user, request).wrapCreated()

    @Operation(summary = "그룹 조회")
    @GetMapping("/api/v1/groups/{id}")
    suspend fun getGroup(
        user: AuthUser,
        @PathVariable("id") id: Long,
    ) = groupService.getGroup(id).wrapOk()
}
