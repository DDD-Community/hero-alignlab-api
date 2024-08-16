package com.hero.alignlab.domain.notification.application

import com.hero.alignlab.common.extension.executes
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.notification.domain.PoseNotification
import com.hero.alignlab.domain.notification.infrastructure.PoseNotificationRepository
import com.hero.alignlab.domain.notification.model.request.PatchPoseNotificationRequest
import com.hero.alignlab.domain.notification.model.request.RegisterPoseNotificationRequest
import com.hero.alignlab.domain.notification.model.response.GetPoseNotificationResponse
import com.hero.alignlab.domain.notification.model.response.PatchPoseNotificationResponse
import com.hero.alignlab.domain.notification.model.response.RegisterPoseNotificationResponse
import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.NotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class PoseNotificationService(
    private val poseNotificationRepository: PoseNotificationRepository,
    private val txTemplates: TransactionTemplates,
) {
    suspend fun getNotification(user: AuthUser): GetPoseNotificationResponse? {
        val poseNotification = findByUidAndIsActiveOrNull(user.uid, true) ?: return null
        return GetPoseNotificationResponse.from(poseNotification)
    }

    suspend fun findByUidAndIsActiveOrNull(uid: Long, isActive: Boolean): PoseNotification? {
        return withContext(Dispatchers.IO) {
            poseNotificationRepository.findByUidAndIsActive(uid, isActive)
        }
    }

    suspend fun registerNotification(
        user: AuthUser,
        request: RegisterPoseNotificationRequest
    ): RegisterPoseNotificationResponse {
        val poseNotification = findByUidOrNull(user.uid)

        val registeredPoseNotification = txTemplates.writer.executes {
            poseNotification?.apply {
                this.isActive = request.isActive
                this.duration = request.duration
            } ?: PoseNotification(
                uid = user.uid,
                isActive = request.isActive,
                duration = request.duration
            ).run { poseNotificationRepository.save(this) }
        }

        return RegisterPoseNotificationResponse.from(registeredPoseNotification)
    }

    suspend fun findByUidOrNull(uid: Long): PoseNotification? {
        return withContext(Dispatchers.IO) {
            poseNotificationRepository.findByUid(uid)
        }
    }

    suspend fun findByUidOrThrow(uid: Long): PoseNotification {
        return findByUidOrNull(uid) ?: throw NotFoundException(ErrorCode.NOT_FOUND_POSE_NOTIFICATION_ERROR)
    }

    suspend fun patch(user: AuthUser, request: PatchPoseNotificationRequest): PatchPoseNotificationResponse {
        val poseNotification = findByUidOrThrow(user.uid)

        val updatedPoseNotification = txTemplates.writer.executes {
            poseNotification.apply {
                request.isActive?.let { isActive -> this.isActive = isActive }
                request.duration?.let { duration -> this.duration = duration }
            }.run { poseNotificationRepository.save(this) }
        }

        return PatchPoseNotificationResponse.from(updatedPoseNotification)
    }
}
