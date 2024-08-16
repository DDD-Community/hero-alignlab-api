package com.hero.alignlab.domain.notification.infrastructure

import com.hero.alignlab.domain.notification.domain.PoseNotification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
interface PoseNotificationRepository : JpaRepository<PoseNotification, Long> {
    fun findByUidAndIsActive(uid: Long, isActive: Boolean): PoseNotification?

    fun findByUid(uid: Long): PoseNotification?
}
