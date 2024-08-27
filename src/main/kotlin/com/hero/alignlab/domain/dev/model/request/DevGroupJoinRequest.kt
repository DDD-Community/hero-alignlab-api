package com.hero.alignlab.domain.dev.model.request

data class DevGroupJoinRequest(
    val uid: Long,
    val joinCode: String? = null
)
