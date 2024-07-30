package com.hero.alignlab.client.kakao.config

import com.hero.alignlab.client.WebClientFactory
import com.hero.alignlab.client.kakao.client.KakaoInfoClient
import com.hero.alignlab.client.kakao.client.KakaoOAuthClient
import com.hero.alignlab.client.kakao.client.SuspendableKakaoInfoClient
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
class KakaoInfoClientConfig {
    private val logger = KotlinLogging.logger { }

    @Bean
    @ConditionalOnProperty(prefix = "client.kakao-info", name = ["url"])
    @ConfigurationProperties(prefix = "client.kakao-info")
    fun kakaoInfoConfig() = Config()

    @Bean
    @ConditionalOnBean(name = ["kakaoInfoConfig"])
    @ConditionalOnMissingBean(KakaoOAuthClient::class)
    fun kakaoInfoClient(
        @Valid kakaoInfoConfig: Config
    ): KakaoInfoClient {
        logger.info { "initialized KaKaoInfoClient. $kakaoInfoConfig" }

        val webclient = WebClientFactory.generate(kakaoInfoConfig.url)

        return SuspendableKakaoInfoClient(webclient, kakaoInfoConfig)
    }

    data class Config(
        @field:NotBlank
        var url: String = "",
        @field:NotBlank
        var unlinkPath: String = "",
    )
}
