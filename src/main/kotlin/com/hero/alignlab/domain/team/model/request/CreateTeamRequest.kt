package com.hero.alignlab.domain.team.model.request

data class CreateTeamRequest(
    /** 팀명, 중복 불가능 */
    val name: String,
    /** 팀 설명 */
    val description: String?,
)
