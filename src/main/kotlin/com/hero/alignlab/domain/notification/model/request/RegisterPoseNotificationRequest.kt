package com.hero.alignlab.domain.notification.model.request

import com.hero.alignlab.domain.notification.domain.vo.PoseNotificationDuration

data class RegisterPoseNotificationRequest(
    val isActive: Boolean,
    val duration: PoseNotificationDuration,
)
