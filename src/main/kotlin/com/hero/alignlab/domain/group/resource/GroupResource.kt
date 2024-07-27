package com.hero.alignlab.domain.group.resource

import com.hero.alignlab.common.extension.wrapCreated
import com.hero.alignlab.common.extension.wrapOk
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.group.application.GroupFacade
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
    private val groupFacade: GroupFacade,
) {
    @Operation(summary = "그룹 생성")
    @PostMapping("/api/v1/groups")
    suspend fun createGroup(
        user: AuthUser,
        @RequestBody request: CreateGroupRequest
    ) = groupService.createGroup(user, request).wrapCreated()

    @Operation(summary = "그룹 탈퇴하기")
    @PostMapping("/api/v1/groups/{id}/withdraw")
    suspend fun withdrawGroup(
        user: AuthUser,
        @PathVariable id: Long
    ) = groupFacade.withdraw(user, id).wrapOk()

    @Operation(summary = "그룹 들어가기")
    @PostMapping("/api/v1/groups/{groupId}/join")
    suspend fun joinGroup(
        user: AuthUser,
        @PathVariable groupId: Long
    ) = groupFacade.joinGroup(user, groupId).wrapOk()
}
