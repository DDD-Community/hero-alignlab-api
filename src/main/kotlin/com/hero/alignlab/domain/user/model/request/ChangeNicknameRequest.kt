package com.hero.alignlab.domain.user.model.request

import jakarta.validation.constraints.Size

data class ChangeNicknameRequest(
    @field:Size(max = 200)
    val nickname: String,
)
