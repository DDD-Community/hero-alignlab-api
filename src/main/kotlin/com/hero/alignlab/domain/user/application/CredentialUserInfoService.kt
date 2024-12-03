package com.hero.alignlab.domain.user.application

import com.hero.alignlab.domain.user.domain.CredentialUserInfo
import com.hero.alignlab.domain.user.infrastructure.CredentialUserInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CredentialUserInfoService(
    private val credentialUserInfoRepository: CredentialUserInfoRepository,
) {
    suspend fun existsByUsername(username: String): Boolean {
        return withContext(Dispatchers.IO) {
            existsByUsernameSync(username)
        }
    }

    private fun existsByUsernameSync(username: String): Boolean {
        return credentialUserInfoRepository.existsByUsername(username)
    }

    @Transactional
    fun saveSync(credentialUserInfo: CredentialUserInfo): CredentialUserInfo {
        return credentialUserInfoRepository.save(credentialUserInfo)
    }

    suspend fun findAllByUid(uid: Long): List<CredentialUserInfo> {
        return withContext(Dispatchers.IO) {
            credentialUserInfoRepository.findAllByUid(uid)
        }
    }
}
