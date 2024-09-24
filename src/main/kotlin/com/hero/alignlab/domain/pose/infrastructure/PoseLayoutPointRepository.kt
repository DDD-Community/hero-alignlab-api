package com.hero.alignlab.domain.pose.infrastructure

import com.hero.alignlab.domain.pose.domain.PoseLayoutPoint
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Repository
interface PoseLayoutPointRepository : JpaRepository<PoseLayoutPoint, Long> {
    fun findAllByPoseLayoutId(poseLayoutPointId: Long): List<PoseLayoutPoint>

    fun deleteAllByPoseLayoutIdIn(poseLayoutPointIds: List<Long>)
}
