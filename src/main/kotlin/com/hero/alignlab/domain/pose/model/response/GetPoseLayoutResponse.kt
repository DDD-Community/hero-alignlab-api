package com.hero.alignlab.domain.pose.model.response

data class GetPoseLayoutResponse(
    /** id가 -1인 경우, 데이터가 없음을 의미 */
    val id: Long = -1,
    val points: List<PoseLayoutPointResponse> = emptyList(),
)
