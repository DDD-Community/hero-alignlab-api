package com.hero.alignlab.domain.user.infrastructure

import com.hero.alignlab.domain.user.domain.vo.OAuthProvider
import com.hero.alignlab.domain.user.domain.OAuthUserInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Repository
interface OAuthUserInfoRepository : JpaRepository<OAuthUserInfo, Long> {
    fun existsByOauthIdAndProvider(oauthId: String, provider: OAuthProvider): Boolean

    fun deleteByOauthIdAndProvider(oauthId: String, provider: OAuthProvider)

    fun findByProviderAndOauthId(provider: OAuthProvider, oauthId: String): OAuthUserInfo?

    fun countByCreatedAtBetween(startAt: LocalDateTime, endAt: LocalDateTime): Long
}
