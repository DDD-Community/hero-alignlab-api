package com.hero.alignlab.domain.group.resource

import com.hero.alignlab.common.extension.wrapCreated
import com.hero.alignlab.common.extension.wrapOk
import com.hero.alignlab.common.extension.wrapPage
import com.hero.alignlab.common.extension.wrapVoid
import com.hero.alignlab.common.model.HeroPageRequest
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.group.application.GroupFacade
import com.hero.alignlab.domain.group.application.GroupService
import com.hero.alignlab.domain.group.model.request.CreateGroupRequest
import com.hero.alignlab.domain.group.model.request.UpdateGroupRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@Tag(name = "Group API")
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class GroupResource(
    private val groupService: GroupService,
    private val groupFacade: GroupFacade,
) {
    /**
     * - 속해 있는 그룹의 정보를 조회할 수 있다.
     * - 그룹장의 경우, joinCode를 조회할 수 있다.(그룹원은 조회 불가.)
     */
    @Operation(summary = "그룹 조회")
    @GetMapping("/api/v1/groups/{id}")
    suspend fun getGroup(
        user: AuthUser,
        @PathVariable id: Long
    ) = groupFacade.getGroup(user, id).wrapOk()

    /**
     * - 정렬 조건
     *      - 크루원 많은 순 : userCount,desc
     *      - 최신 생성 크루 순 : createdAt,desc
     */
    @Operation(summary = "그룹 전체 보기")
    @GetMapping("/api/v1/groups")
    suspend fun searchGroups(
        user: AuthUser,
        @ParameterObject pageRequest: HeroPageRequest,
    ) = groupFacade.searchGroup(user, pageRequest).wrapPage()

    @Operation(summary = "그룹 생성")
    @PostMapping("/api/v1/groups")
    suspend fun createGroup(
        user: AuthUser,
        @RequestBody request: CreateGroupRequest
    ) = groupFacade.createGroup(user, request).wrapCreated()

    @Operation(summary = "그룹 수정하기")
    @PutMapping("/api/v1/groups/{id}")
    suspend fun updateGroup(
        user: AuthUser,
        @PathVariable id: Long,
        @RequestBody request: UpdateGroupRequest
    ) = groupService.updateGroup(
        user = user,
        id = id,
        request = request
    ).wrapOk()

    @Operation(summary = "그룹 탈퇴하기")
    @DeleteMapping("/api/v1/groups/{id}/withdraw")
    suspend fun withdrawGroup(
        user: AuthUser,
        @PathVariable id: Long
    ) = groupFacade.withdraw(user, id).wrapVoid()

    /**
     * - 숨김처리된 그룹의 경우, joinCode가 입력되어야 접속 가능하다.
     */
    @Operation(summary = "그룹 들어가기")
    @PostMapping("/api/v1/groups/{groupId}/join")
    suspend fun joinGroup(
        user: AuthUser,
        @PathVariable groupId: Long,
        @RequestParam(required = false) joinCode: String?,
    ) = groupFacade.joinGroup(
        user = user,
        groupId = groupId,
        joinCode = joinCode
    ).wrapOk()
}
