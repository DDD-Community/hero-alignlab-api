package com.hero.alignlab.domain.discussion.model.request

import com.hero.alignlab.domain.discussion.domain.DiscussionType

data class DiscussionRequest(
    /** 문의하기 유형 */
    val type: DiscussionType,
    /** 문의하기 제목 */
    val title: String,
    /** 문의하기 본문 */
    val content: String,
)
