package com.hero.alignlab.domain.user.model.response

data class CheckChangeNicknameResponse(
    val valid: Boolean,
    val reason: String? = null,
)
