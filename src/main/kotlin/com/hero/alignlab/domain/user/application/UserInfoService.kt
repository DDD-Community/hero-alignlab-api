package com.hero.alignlab.domain.user.application

import com.hero.alignlab.common.encrypt.EncryptData
import com.hero.alignlab.common.encrypt.Encryptor
import com.hero.alignlab.domain.user.domain.OAuthProvider
import com.hero.alignlab.domain.user.domain.UserInfo
import com.hero.alignlab.domain.user.infrastructure.UserInfoRepository
import com.hero.alignlab.domain.user.model.response.UserInfoResponse
import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.NotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserInfoService(
    private val userInfoRepository: UserInfoRepository,
    private val encryptor: Encryptor,
) {
    fun getUserByIdOrThrowSync(id: Long): UserInfo {
        return getUserByIdOrNullSync(id)
            ?: throw NotFoundException(ErrorCode.NOT_FOUND_USER_ERROR)
    }

    fun getUserByIdOrNullSync(id: Long): UserInfo? {
        return userInfoRepository.findByIdOrNull(id)
    }

    fun saveSync(userInfo: UserInfo): UserInfo {
        return userInfoRepository.save(userInfo)
    }

    suspend fun getUserInfo(id: Long): UserInfoResponse {
        val userInfo = withContext(Dispatchers.IO) {
            getUserByIdOrThrowSync(id)
        }

        return UserInfoResponse.from(userInfo)
    }

    suspend fun findByCredential(username: String, password: String): UserInfo {
        return withContext(Dispatchers.IO) {
            findByCredentialSync(username, password)
        } ?: throw NotFoundException(ErrorCode.NOT_FOUND_USER_ERROR)
    }

    private fun findByCredentialSync(username: String, password: String): UserInfo? {
        return userInfoRepository.findByCredential(
            username = username,
            password = EncryptData.enc(password, encryptor)
        )
    }

    suspend fun findAllByIds(ids: List<Long>): List<UserInfo> {
        return withContext(Dispatchers.IO) {
            findAllByIdsSync(ids)
        }
    }

    fun findAllByIdsSync(ids: List<Long>): List<UserInfo> {
        return userInfoRepository.findAllById(ids)
    }

    suspend fun findByOAuthOrThrow(provider: OAuthProvider, oauthId: String): UserInfo {
        return withContext(Dispatchers.IO) {
            userInfoRepository.findByOAuth(provider, oauthId)
        } ?: throw NotFoundException(ErrorCode.NOT_FOUND_USER_ERROR)
    }

    @Transactional
    fun deleteBySync(id: Long) {
        userInfoRepository.deleteById(id)
    }

    suspend fun findAllUids(): List<Long> {
        return withContext(Dispatchers.IO) {
            userInfoRepository.findAllUids()
        }
    }
}
