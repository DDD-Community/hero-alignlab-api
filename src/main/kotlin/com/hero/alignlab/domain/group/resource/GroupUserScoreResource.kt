package com.hero.alignlab.domain.group.resource

import com.hero.alignlab.common.extension.wrapOk
import com.hero.alignlab.common.model.Response
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.group.application.GroupFacade
import com.hero.alignlab.domain.group.model.response.GetGroupRanksResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Group User Score API")
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class GroupUserScoreResource(
    private val groupFacade: GroupFacade,
) {
    /** 내가 속한 그룹의 전체 랭킹을 조회할 수 있다 */
    @Operation(summary = "바른 자세 랭킹")
    @GetMapping("/api/v1/group-scores")
    suspend fun getScores(
        user: AuthUser,
        @RequestParam groupId: Long,
    ): ResponseEntity<Response<GetGroupRanksResponse>> {
        return groupFacade.getGroupRank(user, groupId).wrapOk()
    }
}
