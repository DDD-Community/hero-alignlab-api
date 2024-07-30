package com.hero.alignlab.domain.pose.application

import com.hero.alignlab.domain.pose.domain.PoseSnapshot
import com.hero.alignlab.domain.pose.infrastructure.PoseSnapshotRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PoseSnapshotService(
    private val poseSnapshotRepository: PoseSnapshotRepository,
) {
    @Transactional
    fun saveSync(poseSnapshot: PoseSnapshot): PoseSnapshot {
        return poseSnapshotRepository.save(poseSnapshot)
    }
}
