package com.hero.alignlab.domain.group.model.response

import com.hero.alignlab.domain.group.domain.Group
import com.hero.alignlab.domain.group.domain.GroupTag

data class CreateGroupResponse(
    /** group id */
    val id: Long,
    /** 그룹명 */
    val name: String,
    /** 그룹 설명 */
    val description: String?,
    /** 그룹 태그 리스트 */
    val tags: List<GroupTagResponse>?,
) {
    companion object {
        fun from(group: Group, tags: List<GroupTag>): CreateGroupResponse {
            return CreateGroupResponse(
                id = group.id,
                name = group.name,
                description = group.description,
                tags = tags.map { GroupTagResponse(it.id, it.name) },
            )
        }
    }
}
