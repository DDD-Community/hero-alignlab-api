package com.hero.alignlab.domain.pose.infrastructure.model

import java.time.LocalDate

data class PostCountSearchSpec(
    val uid: Long,
    val fromDate: LocalDate,
    val toDate: LocalDate,
)
