package com.hero.alignlab.domain.auth.model

import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.NotFoundException
import org.springframework.http.HttpHeaders

const val DEV_AUTH_TOKEN_KEY = "X-HERO-DEV-TOKEN"

interface DevAuthUser

class DevAuthUserImpl : DevAuthUser

data class DevAuthToken(
    val key: String = DEV_AUTH_TOKEN_KEY,
    val value: String,
) {
    companion object {
        fun HttpHeaders.resolveDevToken(): DevAuthToken {
            return this.asSequence()
                .filter { header -> isTokenHeader(header.key) }
                .mapNotNull { header ->
                    header.value
                        .firstOrNull()
                        ?.takeIf { token -> token.isNotBlank() }
                        ?.let { token -> from(token) }
                }.firstOrNull() ?: throw NotFoundException(ErrorCode.NOT_FOUND_TOKEN_ERROR)
        }

        private fun isTokenHeader(headerKey: String): Boolean {
            return DEV_AUTH_TOKEN_KEY.equals(headerKey, ignoreCase = true)
        }

        private fun from(value: String): DevAuthToken {
            return DevAuthToken(value = value)
        }
    }
}
