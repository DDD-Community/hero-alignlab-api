package com.hero.alignlab.domain.dev.model.request

import com.hero.alignlab.domain.pose.domain.vo.PoseType

data class DevBulkCreatePoseSnapshot(
    val targets: List<CreateModel>,
) {
    data class CreateModel(
        val uid: Long,
        val types: Map<PoseType, Int>
    )
}
