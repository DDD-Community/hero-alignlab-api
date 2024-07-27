package com.hero.alignlab.domain.group.model.response

import com.hero.alignlab.domain.group.domain.Group

data class UpdateGroupResponse(
    /** group id */
    val id: Long,
    /** 그룹명 */
    val name: String,
    /** 그룹 설명 */
    val description: String?,
) {
    companion object {
        fun from(group: Group): UpdateGroupResponse {
            return UpdateGroupResponse(
                id = group.id,
                name = group.name,
                description = group.description,
            )
        }
    }
}

