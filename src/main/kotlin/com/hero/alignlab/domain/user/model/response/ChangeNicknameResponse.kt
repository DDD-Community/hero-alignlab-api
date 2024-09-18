package com.hero.alignlab.domain.user.model.response

import com.hero.alignlab.domain.user.domain.UserInfo

data class ChangeNicknameResponse(
    val id: Long,
    val nickname: String,
) {
    companion object {
        fun from(user: UserInfo): ChangeNicknameResponse {
            return ChangeNicknameResponse(
                id = user.id,
                nickname = user.nickname
            )
        }
    }
}
