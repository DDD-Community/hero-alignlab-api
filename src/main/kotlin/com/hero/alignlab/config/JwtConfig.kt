package com.hero.alignlab.config

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "auth.jwt")
@ConfigurationPropertiesBinding
data class JwtConfig(
    @field:NotBlank
    var secret: String = "",
    @field:NotBlank
    var accessExp: Int = 0,
    @field:NotBlank
    var refreshExp: Int = 0,
    val issuer: String = "hero-alignlab-api",
    val audience: String = "hero-alignlab-api",
)
