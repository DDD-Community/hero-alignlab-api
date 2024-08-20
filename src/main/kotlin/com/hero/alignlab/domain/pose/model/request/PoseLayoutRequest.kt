package com.hero.alignlab.domain.pose.model.request

import com.hero.alignlab.domain.pose.domain.vo.PosePosition
import java.math.BigDecimal

data class PoseLayoutRequest(
    val points: List<PoseLayoutPointRequest>
)

data class PoseLayoutPointRequest(
    val y: BigDecimal,
    val x: BigDecimal,
    val position: PosePosition,
)
