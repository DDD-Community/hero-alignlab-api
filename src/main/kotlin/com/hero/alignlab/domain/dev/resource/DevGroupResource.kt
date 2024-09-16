package com.hero.alignlab.domain.dev.resource

import com.hero.alignlab.config.swagger.SwaggerTag.DEV_TAG
import com.hero.alignlab.domain.auth.model.DevAuthUser
import com.hero.alignlab.domain.dev.model.request.DevGroupJoinRequest
import com.hero.alignlab.domain.group.application.GroupFacade
import com.hero.alignlab.domain.group.model.response.JoinGroupResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@Tag(name = DEV_TAG)
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class DevGroupResource(
    private val groupFacade: GroupFacade,
) {
    @Operation(summary = "그룹 조인하기")
    @PostMapping("/api/dev/v1/groups/{groupId}/join")
    suspend fun joinGroup(
        dev: DevAuthUser,
        @PathVariable groupId: Long,
        @RequestBody request: DevGroupJoinRequest,
    ): JoinGroupResponse {
        return groupFacade.joinGroup(
            groupId = groupId,
            uid = request.uid,
            joinCode = request.joinCode
        )
    }
}
