package com.hero.alignlab.domain.team.model.response

import com.hero.alignlab.domain.team.domain.Team

data class CreateTeamResponse(
    /** team id */
    val id: Long,
    /** 팀명 */
    val name: String,
    /** 팀 설명 */
    val description: String?,
) {
    companion object {
        fun from(team: Team): CreateTeamResponse {
            return CreateTeamResponse(
                id = team.id,
                name = team.name,
                description = team.description,
            )
        }
    }
}
