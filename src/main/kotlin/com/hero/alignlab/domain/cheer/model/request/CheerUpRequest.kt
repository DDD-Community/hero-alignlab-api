package com.hero.alignlab.domain.cheer.model.request

data class CheerUpRequest(
    /** 응원할 유저의 uids */
    val uids: Set<Long>
)
