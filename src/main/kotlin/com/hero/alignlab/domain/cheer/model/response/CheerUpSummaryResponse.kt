package com.hero.alignlab.domain.cheer.model.response

data class CheerUpSummaryResponse(
    /** 내가 응원을 받은 횟수 */
    val countCheeredUp: Long,
    /** 내가 응원을 한 유저 uids */
    val cheeredUpUids: List<Long>,
)
