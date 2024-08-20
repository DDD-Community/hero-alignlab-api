package com.hero.alignlab.domain.pose.application

import com.hero.alignlab.domain.pose.domain.PoseLayoutPoint
import com.hero.alignlab.domain.pose.infrastructure.PoseLayoutPointRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PoseLayoutPointService(
    private val poseLayoutPointRepository: PoseLayoutPointRepository,
) {
    suspend fun findAllByPoseLayoutId(poseLayoutPointId: Long): List<PoseLayoutPoint> {
        return withContext(Dispatchers.IO) {
            poseLayoutPointRepository.findAllByPoseLayoutId(poseLayoutPointId)
        }
    }

    @Transactional
    fun saveAllSync(poseLayoutPoints: List<PoseLayoutPoint>): List<PoseLayoutPoint> {
        return poseLayoutPointRepository.saveAll(poseLayoutPoints)
    }
}
