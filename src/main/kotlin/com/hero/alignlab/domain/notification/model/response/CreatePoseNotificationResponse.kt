package com.hero.alignlab.domain.notification.model.response

import com.hero.alignlab.domain.notification.domain.PoseNotification
import com.hero.alignlab.domain.notification.domain.vo.PoseNotificationDuration

data class CreatePoseNotificationResponse(
    val id: Long,
    val isActive: Boolean,
    val duration: PoseNotificationDuration,
) {
    companion object {
        fun from(poseNotification: PoseNotification): CreatePoseNotificationResponse {
            return CreatePoseNotificationResponse(
                id = poseNotification.id,
                isActive = poseNotification.isActive,
                duration = poseNotification.duration
            )
        }
    }
}
