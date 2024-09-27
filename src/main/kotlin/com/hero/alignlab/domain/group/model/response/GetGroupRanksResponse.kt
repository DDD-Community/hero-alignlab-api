package com.hero.alignlab.domain.group.model.response

data class GetGroupRanksResponse(
    val groupId: Long,
    val ranks: List<GetGroupRankResponse>,
    /** 크루 전체 평균 스코어 */
    val avgScore: Int?,
    /** 내 평균 스코어 */
    val myScore: Int?,
)

data class GetGroupRankResponse(
    val groupUserId: Long,
    val name: String,
    val rank: Int,
    val score: Int,
)
