package com.hero.alignlab.domain.user.infrastructure

import com.hero.alignlab.domain.user.domain.OAuthProvider
import com.hero.alignlab.domain.user.domain.OAuthUserInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Repository
interface OAuthUserInfoRepository : JpaRepository<OAuthUserInfo, Long> {
    fun existsByOauthIdAndProvider(oauthId: String, provider: OAuthProvider): Boolean

    fun deleteByOauthIdAndProvider(oauthId: String, provider: OAuthProvider)

    fun findByProviderAndOauthId(provider: OAuthProvider, oauthId: String): OAuthUserInfo?
}
