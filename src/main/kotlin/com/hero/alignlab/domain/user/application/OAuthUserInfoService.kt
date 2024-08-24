package com.hero.alignlab.domain.user.application

import com.hero.alignlab.domain.user.domain.vo.OAuthProvider
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

    @Transactional
    fun deleteSync(provider: OAuthProvider, oauthId: String) {
        oAuthUserInfoRepository.deleteByOauthIdAndProvider(oauthId, provider)
    }

    suspend fun findByProviderAndOauthId(provider: OAuthProvider, oauthId: String): OAuthUserInfo? {
        return withContext(Dispatchers.IO) {
            oAuthUserInfoRepository.findByProviderAndOauthId(provider, oauthId)
        }
    }
}
