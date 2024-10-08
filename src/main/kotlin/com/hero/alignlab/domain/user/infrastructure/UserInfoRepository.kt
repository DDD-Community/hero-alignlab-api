package com.hero.alignlab.domain.user.infrastructure

import com.hero.alignlab.common.encrypt.EncryptData
import com.hero.alignlab.domain.user.domain.QCredentialUserInfo
import com.hero.alignlab.domain.user.domain.QOAuthUserInfo
import com.hero.alignlab.domain.user.domain.QUserInfo
import com.hero.alignlab.domain.user.domain.UserInfo
import com.hero.alignlab.domain.user.domain.vo.OAuthProvider
import com.querydsl.jpa.impl.JPAQuery
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Repository
interface UserInfoRepository : JpaRepository<UserInfo, Long>, UserInfoQRepository {
    fun countByCreatedAtBetween(startAt: LocalDateTime, endAt: LocalDateTime): Long

    fun existsByNicknameAndIdNot(nickname: String, id: Long): Boolean
}

@Transactional(readOnly = true)
interface UserInfoQRepository {
    fun findByCredential(username: String, password: EncryptData): UserInfo?

    fun findByOAuth(provider: OAuthProvider, oauthId: String): UserInfo?

    fun findAllUids(): List<Long>
}

class UserInfoQRepositoryImpl : UserInfoQRepository, QuerydslRepositorySupport(UserInfo::class.java) {
    @Autowired
    @Qualifier("heroEntityManager")
    override fun setEntityManager(entityManager: EntityManager) {
        super.setEntityManager(entityManager)
    }

    private val qUserInfo = QUserInfo.userInfo
    private val qCredentialUserInfo = QCredentialUserInfo.credentialUserInfo
    private val qOAuthUserInfo = QOAuthUserInfo.oAuthUserInfo

    override fun findByCredential(username: String, password: EncryptData): UserInfo? {
        return JPAQuery<QUserInfo>(entityManager)
            .select(qUserInfo)
            .from(qUserInfo)
            .join(qCredentialUserInfo).on(qUserInfo.id.eq(qCredentialUserInfo.uid))
            .where(
                qCredentialUserInfo.username.eq(username),
                qCredentialUserInfo.password.eq(password)
            ).fetchFirst()
    }

    override fun findByOAuth(provider: OAuthProvider, oauthId: String): UserInfo? {
        return JPAQuery<QUserInfo>(entityManager)
            .select(qUserInfo)
            .from(qUserInfo)
            .join(qOAuthUserInfo).on(qUserInfo.id.eq(qOAuthUserInfo.uid))
            .where(
                qOAuthUserInfo.provider.eq(provider),
                qOAuthUserInfo.oauthId.eq(oauthId)
            ).fetchFirst()
    }

    override fun findAllUids(): List<Long> {
        return JPAQuery<QUserInfo>(entityManager)
            .select(qUserInfo.id)
            .from(qUserInfo)
            .fetch()
    }
}
