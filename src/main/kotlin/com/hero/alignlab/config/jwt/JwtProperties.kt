package com.hero.alignlab.config.jwt

import com.hero.alignlab.domain.auth.application.JwtTokenService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(JwtConfig.JwtProperties::class)
class JwtConfig {
    private val logger = KotlinLogging.logger {}

    @ConfigurationProperties(prefix = "auth.jwt")
    data class JwtProperties(
        @field:NotBlank
        var secret: String = "",
        @field:NotBlank
        var accessExp: Int = 0,
        @field:NotBlank
        var refreshExp: Int = 0,
        val issuer: String = "hero-alignlab-api",
        val audience: String = "hero-alignlab-api",
    )

    @Bean
    fun jwtTokenService(jwtProperties: JwtProperties): JwtTokenService {
        logger.info { "initialized jwtTokenService. $jwtProperties" }
        return JwtTokenService(jwtProperties)
    }
}


