package com.hero.alignlab.domain.user.application

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
class UserService(
    private val userInfoRepository: UserInfoRepository,
) {
    fun getUserByIdOrThrowSync(id: Long): UserInfo {
        return userInfoRepository.findByIdOrNull(id)
            ?: throw NotFoundException(ErrorCode.NOT_FOUND_USER_ERROR)
    }

    fun getUserByIdOrNullSync(id: Long): UserInfo? {
        return userInfoRepository.findByIdOrNull(id)
    }

    fun save(userInfo: UserInfo): UserInfo {
        return userInfoRepository.save(userInfo)
    }

    suspend fun getUserInfo(id: Long): UserInfoResponse {
        val userInfo = withContext(Dispatchers.IO) {
            getUserByIdOrThrowSync(id)
        }

        return UserInfoResponse.from(userInfo)
    }
}
