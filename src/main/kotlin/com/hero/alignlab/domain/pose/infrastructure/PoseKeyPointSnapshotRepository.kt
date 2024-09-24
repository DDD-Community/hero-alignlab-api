package com.hero.alignlab.domain.pose.infrastructure

import com.hero.alignlab.domain.pose.domain.PoseKeyPointSnapshot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Repository
interface PoseKeyPointSnapshotRepository : JpaRepository<PoseKeyPointSnapshot, Long> {
    fun deleteAllByPoseSnapshotIdIn(poseKeyPointSnapshots: List<Long>)
}
