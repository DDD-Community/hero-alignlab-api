package com.hero.alignlab.domain.pose.application

import com.hero.alignlab.domain.pose.domain.PoseCount
import com.hero.alignlab.domain.pose.infrastructure.PoseCountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class PoseCountService(
    private val poseCountRepository: PoseCountRepository,
) {
    @Transactional
    fun saveSync(poseCount: PoseCount): PoseCount {
        return poseCountRepository.save(poseCount)
    }

    suspend fun findByDateOrNull(date: LocalDate): PoseCount? {
        return withContext(Dispatchers.IO) {
            poseCountRepository.findByDate(date)
        }
    }
}
