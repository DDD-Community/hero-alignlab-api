package com.hero.alignlab.domain.user.application

import com.hero.alignlab.common.encrypt.EncryptData
import com.hero.alignlab.common.encrypt.Encryptor
import com.hero.alignlab.domain.user.domain.UserInfo
import com.hero.alignlab.domain.user.infrastructure.UserInfoRepository
import com.hero.alignlab.domain.user.model.response.UserInfoResponse
import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.NotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

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
            userInfoRepository.findByCredential(
                username = username,
                password = EncryptData.enc(password, encryptor)
            )
        } ?: throw NotFoundException(ErrorCode.NOT_FOUND_USER_ERROR)
    }
}
