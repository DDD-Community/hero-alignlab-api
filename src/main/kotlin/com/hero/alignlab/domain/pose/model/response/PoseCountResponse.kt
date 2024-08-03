package com.hero.alignlab.domain.pose.model.response

import com.hero.alignlab.domain.pose.domain.vo.PoseType
import java.time.LocalDate

data class PoseCountResponse(
    val date: LocalDate,
    val count: Map<PoseType, Int>
)
