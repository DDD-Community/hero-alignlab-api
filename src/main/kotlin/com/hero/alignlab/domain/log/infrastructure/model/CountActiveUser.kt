package com.hero.alignlab.domain.log.infrastructure.model

import com.querydsl.core.annotations.QueryProjection

data class CountActiveUser @QueryProjection constructor(
    val uid: Long,
    val count: Long,
)
