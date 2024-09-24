package com.hero.alignlab.domain.user.model.response

import com.hero.alignlab.domain.user.domain.UserInfo

data class UserInfoResponse(
    val uid: Long,
    val nickname: String,
    val level: Int,
) {
    companion object {
        fun from(user: UserInfo): UserInfoResponse {
            return UserInfoResponse(
                uid = user.id,
                nickname = user.nickname,
                level = user.level,
            )
        }
    }
}
