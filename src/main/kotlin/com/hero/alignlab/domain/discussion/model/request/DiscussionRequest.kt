package com.hero.alignlab.domain.discussion.model.request

import com.hero.alignlab.domain.discussion.domain.vo.DiscussionType

data class DiscussionRequest(
    /** 이메일, nullable하게 데이터를 받음. */
    val email: String? = null,
    /** 문의하기 유형 */
    val type: DiscussionType,
    /** 문의하기 제목 */
    val title: String,
    /** 문의하기 본문 */
    val content: String,
)
