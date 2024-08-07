package com.hero.alignlab.domain.pose.application

import com.hero.alignlab.domain.pose.domain.PoseSnapshot
import com.hero.alignlab.domain.pose.infrastructure.PoseSnapshotRepository
import com.hero.alignlab.domain.pose.infrastructure.model.PoseTypeCountModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class PoseSnapshotService(
    private val poseSnapshotRepository: PoseSnapshotRepository,
) {
    @Transactional
    fun saveSync(poseSnapshot: PoseSnapshot): PoseSnapshot {
        return poseSnapshotRepository.save(poseSnapshot)
    }

    suspend fun countByUidsAndDate(uids: List<Long>, date: LocalDate): List<PoseTypeCountModel> {
        return withContext(Dispatchers.IO) {
            poseSnapshotRepository.countByUidsAndDate(uids, date)
        }
    }
}
