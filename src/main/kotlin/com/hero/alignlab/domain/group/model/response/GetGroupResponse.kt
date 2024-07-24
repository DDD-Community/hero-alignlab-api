package com.hero.alignlab.domain.group.model.response

import com.hero.alignlab.domain.group.domain.Group
import java.time.LocalDateTime

data class GetGroupResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val ownerUid: Long,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
) {
    companion object {
        fun from(group: Group): GetGroupResponse {
            return GetGroupResponse(
                id = group.id,
                name = group.name,
                description = group.description,
                ownerUid = group.ownerUid,
                createdAt = group.createdAt,
                modifiedAt = group.modifiedAt
            )
        }
    }
}
