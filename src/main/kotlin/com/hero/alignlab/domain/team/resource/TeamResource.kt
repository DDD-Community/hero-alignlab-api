package com.hero.alignlab.domain.team.resource

import com.hero.alignlab.common.extension.wrapCreated
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.team.application.TeamService
import com.hero.alignlab.domain.team.model.request.CreateTeamRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Team API")
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class TeamResource(
    private val teamService: TeamService,
) {
    @Operation(summary = "팀 생성")
    @PostMapping("/api/v1/teams")
    suspend fun createTeam(
        user: AuthUser,
        @RequestBody request: CreateTeamRequest
    ) = teamService.createTeam(user, request).wrapCreated()
}
