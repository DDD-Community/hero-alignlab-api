package com.hero.alignlab.domain.user.application

import com.hero.alignlab.domain.user.domain.OAuthProvider
import com.hero.alignlab.domain.user.domain.OAuthUserInfo
import com.hero.alignlab.domain.user.infrastructure.OAuthUserInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OAuthUserInfoService(
    private val oAuthUserInfoRepository: OAuthUserInfoRepository,
) {
    @Transactional
    fun saveSync(userInfo: OAuthUserInfo): OAuthUserInfo {
        return oAuthUserInfoRepository.save(userInfo)
    }

    suspend fun existsByOauthIdAndProvider(oauthId: String, provider: OAuthProvider): Boolean {
        return withContext(Dispatchers.IO) {
            oAuthUserInfoRepository.existsByOauthIdAndProvider(oauthId, provider)
        }
    }
}
