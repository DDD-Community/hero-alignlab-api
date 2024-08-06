package com.hero.alignlab.domain.user.infrastructure

import com.hero.alignlab.common.encrypt.EncryptData
import com.hero.alignlab.domain.user.domain.*
import com.querydsl.jpa.impl.JPAQuery
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Repository
interface UserInfoRepository : JpaRepository<UserInfo, Long>, UserInfoQRepository

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
        QOAuthUserInfo.oAuthUserInfo
        return JPAQuery<UserInfo>(entityManager)
            .select(qUserInfo)
            .from(qUserInfo)
            .join(qCredentialUserInfo).on(qUserInfo.id.eq(qCredentialUserInfo.uid))
            .where(
                qCredentialUserInfo.username.eq(username),
                qCredentialUserInfo.password.eq(password)
            ).fetchFirst()
    }

    override fun findByOAuth(provider: OAuthProvider, oauthId: String): UserInfo? {
        return JPAQuery<UserInfo>(entityManager)
            .select(qUserInfo)
            .from(qUserInfo)
            .join(qOAuthUserInfo).on(qUserInfo.id.eq(qOAuthUserInfo.uid))
            .where(
                qOAuthUserInfo.provider.eq(provider),
                qOAuthUserInfo.oauthId.eq(oauthId)
            ).fetchFirst()
    }

    override fun findAllUids(): List<Long> {
        return JPAQuery<UserInfo>(entityManager)
            .select(qUserInfo.id)
            .from(qUserInfo)
            .fetch()
    }
}
