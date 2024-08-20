package com.hero.alignlab.domain.pose.model.response

import com.hero.alignlab.domain.pose.domain.PoseLayoutPoint
import com.hero.alignlab.domain.pose.domain.vo.PosePosition
import java.math.BigDecimal

data class GetRecentPoseLayoutResponse(
    /** id가 -1인 경우, 데이터가 없음을 의미 */
    val id: Long = -1,
    val points: List<PoseLayoutPointResponse> = emptyList(),
)

data class PoseLayoutPointResponse(
    val position: PosePosition,
    val x: BigDecimal,
    val y: BigDecimal,
) {
    companion object {
        fun from(poseLayoutPoint: PoseLayoutPoint): PoseLayoutPointResponse {
            return PoseLayoutPointResponse(
                position = poseLayoutPoint.position,
                x = poseLayoutPoint.x,
                y = poseLayoutPoint.y,
            )
        }
    }
}
