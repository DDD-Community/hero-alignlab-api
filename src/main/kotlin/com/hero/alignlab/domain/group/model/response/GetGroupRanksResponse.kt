package com.hero.alignlab.domain.group.model.response

data class GetGroupRanksResponse(
    val groupId: Long,
    val ranks: List<GetGroupRankResponse>
)

data class GetGroupRankResponse(
    val groupUserId: Long,
    val name: String,
    val rank: Int,
    val score: Int,
)
