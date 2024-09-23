package com.hero.alignlab.domain.notification.model.request

import com.hero.alignlab.domain.notification.domain.vo.PoseNotificationDuration

data class CreateOrUpdatePoseNotificationRequest(
    /** 자세 알림 활성화 여부 */
    val isActive: Boolean,
    /** 자세 알림 주기 */
    val duration: PoseNotificationDuration,
)
