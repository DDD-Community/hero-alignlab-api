package com.hero.alignlab.domain.group.model.response

import com.hero.alignlab.domain.group.domain.Group

data class MyGroupResponse(
    /** group id */
    val id: Long,
    /** 그룹명 */
    val name: String,
    /** 그룹 설명 */
    val description: String?,
    /** 그룹원 수 */
    val userCount: Int,
    /** 그룹 정원 */
    val userCapacity: Int,
    /** 그룹장 명 */
    val ownerNickname: String,
) {
    companion object {
        fun of(group: Group, userCount: Int, nickname: String): MyGroupResponse {
            return MyGroupResponse(
                id = group.id,
                name = group.name,
                description = group.description,
                userCount = userCount,
                userCapacity = group.userCapacity,
                ownerNickname = nickname
            )
        }
    }
}
