package com.hero.alignlab.domain.pose.model.request

import java.time.LocalDate

data class PoseSearchRequest(
    val fromDate: LocalDate,
    val toDate: LocalDate,
)
