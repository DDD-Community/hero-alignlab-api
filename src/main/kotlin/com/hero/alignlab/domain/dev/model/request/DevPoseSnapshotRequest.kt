package com.hero.alignlab.domain.dev.model.request

import java.time.LocalDateTime

data class DevPoseSnapshotRequest(
    val uid: Long,
    val fromDate: LocalDateTime,
    val toDate: LocalDateTime,
    val dailyCount: Int,
)
