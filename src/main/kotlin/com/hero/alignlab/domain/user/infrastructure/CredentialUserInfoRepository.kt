package com.hero.alignlab.domain.user.infrastructure

import com.hero.alignlab.common.encrypt.EncryptData
import com.hero.alignlab.domain.user.domain.CredentialUserInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Repository
interface CredentialUserInfoRepository : JpaRepository<CredentialUserInfo, Long> {
    fun existsByUsername(username: String): Boolean

    fun findByUsernameAndPassword(username: String, password: EncryptData): CredentialUserInfo?

    fun countByCreatedAtBetween(startAt: LocalDateTime, endAt: LocalDateTime): Long

    fun deleteAllByUid(uid: Long)

    fun findAllByUid(uid: Long): List<CredentialUserInfo>
}
