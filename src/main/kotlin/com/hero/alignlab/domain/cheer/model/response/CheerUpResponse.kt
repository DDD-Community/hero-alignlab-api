package com.hero.alignlab.domain.cheer.model.response

data class CheerUpResponse(
    /** 응원하기가 성공한 케이스에 대해 응답 */
    val uids: Set<Long>,
)
