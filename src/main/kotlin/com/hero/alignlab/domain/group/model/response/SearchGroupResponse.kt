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
    /** 그룹에 속해 있는지 여부 */
    val hasJoined: Boolean,
    /** 그룹 태그 리스트 */
    val tagNames: List<String>,
) {
    companion object {
        fun from(group: Group, hasJoined: Boolean, tagNames: List<String>?): SearchGroupResponse {
            return SearchGroupResponse(
                id = group.id,
                userCount = group.userCount,
                userCapacity = group.userCapacity,
                name = group.name,
                isHidden = group.isHidden,
                hasJoined = hasJoined,
                tagNames = tagNames ?: emptyList(),
            )
        }
    }
}
