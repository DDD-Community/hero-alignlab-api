package com.hero.alignlab.domain.group.model.request

data class CreateGroupRequest(
    /** 그룹명, 중복 불가능 */
    val name: String,
    /** 그룹 설명 */
    val description: String?,
)
