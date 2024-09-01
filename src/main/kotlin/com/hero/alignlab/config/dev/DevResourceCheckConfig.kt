package com.hero.alignlab.config.dev

import com.hero.alignlab.domain.auth.model.DevAuthToken
import com.hero.alignlab.domain.auth.model.DevAuthUserImpl
import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.NoAuthorityException
import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Mono

@Configuration
@EnableConfigurationProperties(DevResourceCheckConfig.DevResourceCheckProperties::class)
class DevResourceCheckConfig(
    _devResourceCheckProperties: DevResourceCheckProperties
) {
    @ConfigurationProperties(prefix = "hero-alignlab.dev.resource")
    data class DevResourceCheckProperties(
        @field:NotBlank
        var key: String = "",
    )

    init {
        devResourceCheckProperties = _devResourceCheckProperties
    }

    companion object {
        const val DEV_AUTH_TOKEN_KEY = "X-HERO-DEV-AUTH-TOKEN"

        private lateinit var devResourceCheckProperties: DevResourceCheckProperties

        suspend fun <T> devResource(key: String, function: suspend () -> T): T {
            if (devResourceCheckProperties.key == key) {
                return function.invoke()
            }
            throw NoAuthorityException(ErrorCode.NO_AUTHORITY_ERROR)
        }
    }

    fun checkDev(token: Mono<DevAuthToken>): Mono<Any> {
        return token.flatMap {
            if (it.value != devResourceCheckProperties.key) {
                Mono.error(NoAuthorityException(ErrorCode.NO_AUTHORITY_ERROR))
            } else {
                Mono.just(DevAuthUserImpl())
            }
        }
    }
}
