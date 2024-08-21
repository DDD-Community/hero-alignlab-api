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
) {
    companion object {
        fun from(group: Group): GetGroupResponse {
            return GetGroupResponse(
                group.id,
                group.name,
                group.description,
                group.ownerUid,
                group.isHidden,
                group.joinCode,
                group.userCount,
                group.userCapacity
            )
        }
    }
}
