package com.hero.alignlab.domain.pose.application

import com.hero.alignlab.domain.pose.domain.PoseKeyPointSnapshot
import com.hero.alignlab.domain.pose.infrastructure.PoseKeyPointSnapshotRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PoseKeyPointSnapshotService(
    private val poseKeyPointSnapshotRepository: PoseKeyPointSnapshotRepository,
) {
    @Transactional
    fun saveAllSync(poseKeyPointSnapshots: List<PoseKeyPointSnapshot>): List<PoseKeyPointSnapshot> {
        return poseKeyPointSnapshotRepository.saveAll(poseKeyPointSnapshots)
    }
}
