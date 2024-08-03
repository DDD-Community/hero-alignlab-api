package com.hero.alignlab.domain.pose.model.request

import com.hero.alignlab.domain.pose.domain.PoseType
import com.hero.alignlab.domain.pose.model.PoseSnapshotModel

data class PoseSnapshotRequest(
    /** 스냅샷 원천 데이터 */
    val snapshot: PoseSnapshotModel,
    /** 포즈 타입 */
    val type: PoseType,
)
