package com.hero.alignlab.domain.user.model.response

import com.hero.alignlab.domain.user.domain.UserInfo

data class UserInfoResponse(
    val uid: Long,
    val nickname: String,
    val providers: List<AuthProvider> = emptyList(),
    val level: Int,
) {
    companion object {
        fun of(user: UserInfo, providers: List<AuthProvider>): UserInfoResponse {
            return UserInfoResponse(
                uid = user.id,
                nickname = user.nickname,
                providers = providers,
                level = user.level,
            )
        }

        fun of(user: UserInfo): UserInfoResponse {
            return UserInfoResponse(
                uid = user.id,
                nickname = user.nickname,
                level = user.level,
            )
        }
    }
}

enum class AuthProvider {
    BASIC,
    KAKAO,
    ;
}
