package com.hero.alignlab.domain.group.model.response

import com.hero.alignlab.domain.group.domain.Group

data class GetGroupResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val ownerUid: Long,
    val isHidden: Boolean,
    /** 그룹장만 조회 가능 */
    val joinCode: String?,
    /** 그룹원 수 */
    val userCount: Int,
    /** 그룹 정원 */
    val userCapacity: Int,
    val ranks: List<GetGroupRankResponse>? = null
) {
    companion object {
        fun from(group: Group): GetGroupResponse {
            return GetGroupResponse(
                id = group.id,
                name = group.name,
                description = group.description,
                ownerUid = group.ownerUid,
                isHidden = group.isHidden,
                joinCode = group.joinCode,
                userCount = group.userCount,
                userCapacity = group.userCapacity
            )
        }
    }
}
