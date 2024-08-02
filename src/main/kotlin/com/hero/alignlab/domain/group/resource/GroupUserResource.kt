package com.hero.alignlab.domain.group.resource

import com.hero.alignlab.common.extension.wrapPage
import com.hero.alignlab.common.extension.wrapVoid
import com.hero.alignlab.common.model.AlignlabPageRequest
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.group.application.GroupFacade
import com.hero.alignlab.domain.group.application.GroupUserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@Tag(name = "Group User API")
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class GroupUserResource(
    private val groupUserService: GroupUserService,
    private val groupFacade: GroupFacade,
) {
    @Operation(summary = "그룹원 조회")
    @GetMapping("/api/v1/group-users")
    suspend fun groupUsers(
        user: AuthUser,
        @RequestParam groupId: Long,
        @ParameterObject pageRequest: AlignlabPageRequest,
    ) = groupUserService.getGroupUsers(
        user = user,
        groupId = groupId,
        pageable = pageRequest.toDefault()
    ).wrapPage()

    @Operation(summary = "그룹원 내보내기")
    @DeleteMapping("/api/v1/group-users/{groupUserId}")
    suspend fun deleteGroupUser(
        user: AuthUser,
        @PathVariable groupUserId: Long,
    ) = groupFacade.deleteGroupUser(user, groupUserId).wrapVoid()
}
