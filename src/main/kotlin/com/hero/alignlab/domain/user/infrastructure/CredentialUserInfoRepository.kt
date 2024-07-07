package com.hero.alignlab.domain.user.infrastructure

import com.hero.alignlab.common.encrypt.EncryptData
import com.hero.alignlab.domain.user.domain.CredentialUserInfo
import com.hero.alignlab.domain.user.domain.QCredentialUserInfo
import com.hero.alignlab.domain.user.domain.QUserInfo
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Repository
interface CredentialUserInfoRepository : JpaRepository<CredentialUserInfo, Long>, CredentialUserInfoQRepository {
    fun existsByUsername(username: String): Boolean

    fun findByUsernameAndPassword(username: String, password: EncryptData): CredentialUserInfo?
}

@Transactional(readOnly = true)
interface CredentialUserInfoQRepository {

}

class CredentialUserInfoRepositoryImpl : CredentialUserInfoQRepository,
    QuerydslRepositorySupport(CredentialUserInfo::class.java) {
    @Autowired
    @Qualifier("heroEntityManager")
    override fun setEntityManager(entityManager: EntityManager) {
        super.setEntityManager(entityManager)
    }

    private val qCredentialUserInfo = QCredentialUserInfo.credentialUserInfo
    private val qUserInfo = QUserInfo.userInfo


}
