package com.hero.alignlab.domain.auth.model

import com.hero.alignlab.domain.user.domain.vo.OAuthProvider

enum class OAuthProvider {
    kakao,
    ;

    fun toProvider(): OAuthProvider {
        return when (this) {
            kakao -> OAuthProvider.KAKAO
        }
    }
}
