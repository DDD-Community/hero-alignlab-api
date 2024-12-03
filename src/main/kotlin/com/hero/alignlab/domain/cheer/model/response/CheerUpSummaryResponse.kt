package com.hero.alignlab.domain.cheer.model.response

data class CheerUpSummaryResponse(
    val countCheeredUp: Long,
    val cheeredUpUids: List<Long>,
)
