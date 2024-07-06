package com.hero.alignlab.domain.user.application

import com.hero.alignlab.common.encrypt.EncryptData
import com.hero.alignlab.common.encrypt.Encryptor
import com.hero.alignlab.domain.user.domain.CredentialUserInfo
import com.hero.alignlab.domain.user.infrastructure.CredentialUserInfoRepository
import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.NotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class CredentialUserInfoService(
    private val credentialUserInfoRepository: CredentialUserInfoRepository,
    private val encryptor: Encryptor,
) {
    suspend fun existsByUsername(username: String): Boolean {
        return withContext(Dispatchers.IO) {
            credentialUserInfoRepository.existsByUsername(username)
        }
    }

    fun save(credentialUserInfo: CredentialUserInfo): CredentialUserInfo {
        return credentialUserInfoRepository.save(credentialUserInfo)
    }

    suspend fun findByUsernameAndPassword(username: String, password: String): CredentialUserInfo {
        return withContext(Dispatchers.IO) {
            credentialUserInfoRepository.findByUsernameAndPassword(username, EncryptData.enc(password, encryptor))
        } ?: throw NotFoundException(ErrorCode.NOT_FOUND_USER_ERROR)
    }
}
