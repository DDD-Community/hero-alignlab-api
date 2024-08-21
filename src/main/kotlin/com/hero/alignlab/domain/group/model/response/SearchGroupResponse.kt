package com.hero.alignlab.domain.group.model.response

import com.hero.alignlab.domain.group.domain.Group

data class SearchGroupResponse(
    /** group Id */
    val id: Long,
    /** 그룹원 수 */
    val userCount: Int,
    /** 그룹 정원 */
    val userCapacity: Int,
    /** 그룹명 */
    val name: String,
    /** 비밀 그룹 여부 */
    val isHidden: Boolean,
) {
    companion object {
        fun from(group: Group): SearchGroupResponse {
            return SearchGroupResponse(
                id = group.id,
                userCount = group.userCount,
                userCapacity = group.userCapacity,
                name = group.name,
                isHidden = group.isHidden,
            )
        }
    }
}
