package com.hero.alignlab.domain.pose.infrastructure.model

import com.hero.alignlab.domain.pose.domain.vo.PoseType
import com.querydsl.core.annotations.QueryProjection

data class PoseTypeCountModel @QueryProjection constructor(
    val uid: Long,
    val type: PoseType,
    val count: Long,
)
