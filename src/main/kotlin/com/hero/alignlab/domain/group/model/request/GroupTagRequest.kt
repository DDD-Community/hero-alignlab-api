package com.hero.alignlab.domain.group.model.request

data class GroupTagRequest(
    /** 태그 ID */
    val tagId: Long,
    /** 태그명 */
    val tagName: String,
)
