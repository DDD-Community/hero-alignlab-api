package com.hero.alignlab.domain.pose.application

import com.hero.alignlab.common.extension.executes
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.pose.domain.PoseSnapshot
import com.hero.alignlab.domain.pose.model.request.PoseSnapshotRequest
import com.hero.alignlab.domain.pose.model.response.PoseSnapshotResponse
import com.hero.alignlab.event.model.LoadPoseSnapshot
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class PoseSnapshotFacade(
    private val poseSnapshotService: PoseSnapshotService,
    private val txTemplates: TransactionTemplates,
    private val publisher: ApplicationEventPublisher,
) {
    suspend fun loadPoseSnapshot(user: AuthUser, request: PoseSnapshotRequest): PoseSnapshotResponse {
        val createdPoseSnapshot = txTemplates.writer.executes {
            val createdPoseSnapshot = poseSnapshotService.saveSync(
                PoseSnapshot(
                    uid = user.uid,
                    score = request.snapshot.score,
                    type = request.type,
                )
            )

            LoadPoseSnapshot(createdPoseSnapshot, request.snapshot.keypoints)
                .run { publisher.publishEvent(this) }

            createdPoseSnapshot
        }

        return PoseSnapshotResponse.from(createdPoseSnapshot)
    }
}
