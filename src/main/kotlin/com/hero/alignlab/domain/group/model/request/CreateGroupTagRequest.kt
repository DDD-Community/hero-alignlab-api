package com.hero.alignlab.domain.group.model.request

data class CreateGroupTagRequest(
    /** 그룹명, 중복 불가능 */
    val groupId: Long,
    /** 태그 리스트, 최대 3개 */
    val tagNames: List<String>,
)