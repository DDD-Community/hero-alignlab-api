package com.hero.alignlab.domain.pose.model.response

import com.hero.alignlab.domain.pose.domain.PoseSnapshot
import java.time.LocalDateTime

data class PoseSnapshotResponse(
    val id: Long,
    val uid: Long,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(createdPoseSnapshot: PoseSnapshot): PoseSnapshotResponse {
            return PoseSnapshotResponse(
                id = createdPoseSnapshot.id,
                uid = createdPoseSnapshot.uid,
                createdAt = createdPoseSnapshot.createdAt
            )
        }
    }
}
