package com.hero.alignlab.domain.user.application

import arrow.fx.coroutines.parZip
import com.hero.alignlab.common.encrypt.EncryptData
import com.hero.alignlab.common.encrypt.Encryptor
import com.hero.alignlab.common.extension.coExecute
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.user.domain.UserInfo
import com.hero.alignlab.domain.user.domain.vo.OAuthProvider
import com.hero.alignlab.domain.user.infrastructure.UserInfoRepository
import com.hero.alignlab.domain.user.model.request.ChangeNicknameRequest
import com.hero.alignlab.domain.user.model.response.ChangeNicknameResponse
import com.hero.alignlab.domain.user.model.response.CheckChangeNicknameResponse
import com.hero.alignlab.domain.user.model.response.UserInfoResponse
import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.InvalidRequestException
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
    private val txTemplates: TransactionTemplates,
) {
    suspend fun getUserByIdOrThrow(uid: Long): UserInfo {
        return withContext(Dispatchers.IO) {
            getUserByIdOrThrowSync(uid)
        }
    }

    fun getUserByIdOrThrowSync(id: Long): UserInfo {
        return getUserByIdOrNullSync(id)
            ?: throw NotFoundException(ErrorCode.NOT_FOUND_USER_ERROR)
    }

    fun getUserByIdOrNullSync(id: Long): UserInfo? {
        return userInfoRepository.findByIdOrNull(id)
    }

    suspend fun findByIdOrThrow(id: Long): UserInfo {
        return findByIdOrNull(id)
            ?: throw NotFoundException(ErrorCode.NOT_FOUND_USER_ERROR)
    }

    suspend fun findByIdOrNull(id: Long): UserInfo? {
        return withContext(Dispatchers.IO) {
            userInfoRepository.findByIdOrNull(id)
        }
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

    suspend fun findByCredentialOrThrow(username: String, password: String): UserInfo {
        return withContext(Dispatchers.IO) {
            userInfoRepository.findByCredential(
                username = username,
                password = EncryptData.enc(password, encryptor)
            )
        } ?: throw NotFoundException(ErrorCode.NOT_FOUND_USER_ERROR)
    }

    suspend fun findAllByIds(ids: List<Long>): List<UserInfo> {
        return withContext(Dispatchers.IO) {
            userInfoRepository.findAllById(ids)
        }
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

    suspend fun changeNickname(
        user: AuthUser,
        id: Long,
        request: ChangeNicknameRequest,
    ): ChangeNicknameResponse {
        if (user.uid != id) {
            throw InvalidRequestException(ErrorCode.NOT_FOUND_USER_ERROR)
        }

        val updatedUserInfo = parZip(
            { getUserByIdOrThrowSync(id) },
            { existsByNicknameAndIdNot(request.nickname, id) }
        ) { userInfo, existsNickname ->
            if (existsNickname) {
                throw InvalidRequestException(ErrorCode.DUPLICATE_USER_NICKNAME_ERROR)
            }

            txTemplates.writer.coExecute {
                userInfo.apply {
                    this.nickname = request.nickname
                }
            }
        }

        return ChangeNicknameResponse.from(updatedUserInfo)
    }

    suspend fun checkChangeNickname(
        user: AuthUser,
        id: Long,
        request: ChangeNicknameRequest,
    ): CheckChangeNicknameResponse {
        if (user.uid != id) {
            return CheckChangeNicknameResponse(
                valid = false,
                reason = "유저 정보를 변경할 수 있는 권한이 없습니다.",
            )
        }

        runCatching {
            parZip(
                { getUserByIdOrThrowSync(id) },
                { existsByNicknameAndIdNot(request.nickname, id) }
            ) { userInfo, existsNickname ->
                if (existsNickname) {
                    throw InvalidRequestException(ErrorCode.DUPLICATE_USER_NICKNAME_ERROR)
                }
            }
        }.onFailure { e ->
            return CheckChangeNicknameResponse(
                valid = false,
                reason = e.message ?: "닉네임 변경을 할 수 없습니다."
            )
        }

        return CheckChangeNicknameResponse(valid = true)
    }

    suspend fun existsByNicknameAndIdNot(nickname: String, id: Long): Boolean {
        return withContext(Dispatchers.IO) {
            userInfoRepository.existsByNicknameAndIdNot(nickname, id)
        }
    }

    suspend fun findAllById(ids: List<Long>): List<UserInfo> {
        return withContext(Dispatchers.IO) {
            userInfoRepository.findAllById(ids)
        }
    }

    suspend fun saveAll(users: List<UserInfo>): List<UserInfo> {
        return txTemplates.writer.coExecute {
            userInfoRepository.saveAll(users)
        }
    }
}
