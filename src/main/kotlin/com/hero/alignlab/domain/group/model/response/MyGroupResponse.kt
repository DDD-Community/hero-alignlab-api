package com.hero.alignlab.domain.group.model.response

import com.hero.alignlab.domain.group.domain.Group

data class MyGroupResponse(
    val id: Long,
    val name: String,
    val description: String?,
) {
    companion object {
        fun from(group: Group): MyGroupResponse {
            return MyGroupResponse(
                id = group.id,
                name = group.name,
                description = group.description
            )
        }
    }
}
