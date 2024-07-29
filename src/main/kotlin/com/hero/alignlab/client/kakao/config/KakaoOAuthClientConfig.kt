package com.hero.alignlab.client.kakao.config

import com.hero.alignlab.client.WebClientFactory
import com.hero.alignlab.client.kakao.client.KaKaoOAuthClient
import com.hero.alignlab.client.kakao.client.SuspendableKakaoOAuthClient
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

@Validated
@Configuration
class KakaoOAuthClientConfig {
    private val logger = KotlinLogging.logger { }

    @Bean
    @ConditionalOnProperty(prefix = "oauth.kakao", name = ["url"])
    @ConfigurationProperties(prefix = "oauth.kakao")
    fun kakaoOAuthConfig() = Config()

    @Bean
    @ConditionalOnBean(name = ["kakaoOAuthConfig"])
    @ConditionalOnMissingBean(KaKaoOAuthClient::class)
    fun kakaoOAuthClient(
        @Valid kakaoOAuthConfig: Config
    ): KaKaoOAuthClient {
        logger.info { "initialized kakaoOAuthClient. $kakaoOAuthConfig" }

        val webclient = WebClientFactory.generate(kakaoOAuthConfig.url)

        return SuspendableKakaoOAuthClient(webclient, kakaoOAuthConfig)
    }

    data class Config(
        @field:NotBlank
        var url: String = "",
        @field:NotBlank
        var restApiKey: String = "",
        @field:NotBlank
        var clientSecretCode: String = "",
        @field:NotBlank
        var redirectUrl: String = "",
        @field:NotBlank
        var authorizedUrl: String = "",
    )
}
