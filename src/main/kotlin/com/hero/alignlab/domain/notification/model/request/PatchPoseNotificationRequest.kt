package com.hero.alignlab.domain.notification.model.request

import com.hero.alignlab.domain.notification.domain.vo.PoseNotificationDuration

data class PatchPoseNotificationRequest(
    val isActive: Boolean?,
    val duration: PoseNotificationDuration?
)
