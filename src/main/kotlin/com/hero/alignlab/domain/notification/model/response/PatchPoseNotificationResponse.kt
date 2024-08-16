package com.hero.alignlab.domain.notification.model.response

import com.hero.alignlab.domain.notification.domain.PoseNotification
import com.hero.alignlab.domain.notification.domain.vo.PoseNotificationDuration

data class PatchPoseNotificationResponse(
    val id: Long,
    val isActive: Boolean,
    val duration: PoseNotificationDuration,
) {
    companion object {
        fun from(poseNotification: PoseNotification): PatchPoseNotificationResponse {
            return PatchPoseNotificationResponse(
                id = poseNotification.id,
                isActive = poseNotification.isActive,
                duration = poseNotification.duration
            )
        }
    }
}
