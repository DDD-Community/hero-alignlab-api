package com.hero.alignlab.domain.pose.application

import com.hero.alignlab.domain.pose.domain.PoseLayout
import com.hero.alignlab.domain.pose.infrastructure.PoseLayoutRepository
import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.NotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PoseLayoutService(
    private val poseLayoutRepository: PoseLayoutRepository
) {
    suspend fun findTop1ByUidOrderByIdDesc(uid: Long): PoseLayout? {
        return withContext(Dispatchers.IO) {
            poseLayoutRepository.findTop1ByUidOrderByIdDesc(uid)
        }
    }

    suspend fun findByIdAndUidOrThrow(id: Long, uid: Long): PoseLayout {
        return findByIdAndUidOrNull(id, uid) ?: throw NotFoundException(ErrorCode.NOT_FOUND_POSE_LAYOUT_ERROR)
    }

    suspend fun findByIdAndUidOrNull(id: Long, uid: Long): PoseLayout? {
        return withContext(Dispatchers.IO) {
            poseLayoutRepository.findByIdAndUid(id, uid)
        }
    }

    @Transactional
    fun saveSync(poseLayout: PoseLayout): PoseLayout {
        return poseLayoutRepository.save(poseLayout)
    }
}
