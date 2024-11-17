package com.hero.alignlab.domain.notification.domain

import com.hero.alignlab.domain.common.domain.BaseEntity
import com.hero.alignlab.domain.notification.domain.vo.PoseNotificationDuration
import jakarta.persistence.*

/** 자세 알림 */
@Entity
@Table(name = "pose_noti")
class PoseNotification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "uid")
    val uid: Long = 0L,

    @Column(name = "is_active")
    var isActive: Boolean = true,

    @Column(name = "duration")
    @Enumerated(EnumType.STRING)
    var duration: PoseNotificationDuration,
) : BaseEntity()
