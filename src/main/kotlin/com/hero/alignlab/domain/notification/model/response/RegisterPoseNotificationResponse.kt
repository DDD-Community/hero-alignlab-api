package com.hero.alignlab.domain.notification.model.response

import com.hero.alignlab.domain.notification.domain.PoseNotification
import com.hero.alignlab.domain.notification.domain.vo.PoseNotificationDuration

data class RegisterPoseNotificationResponse(
    val id: Long,
    val duration: PoseNotificationDuration,
) {
    companion object {
        fun from(poseNotification: PoseNotification): RegisterPoseNotificationResponse {
            return RegisterPoseNotificationResponse(
                id = poseNotification.id,
                duration = poseNotification.duration
            )
        }
    }
}
