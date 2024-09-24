package com.hero.alignlab.client.discord.config

import com.hero.alignlab.client.WebClientFactory
import com.hero.alignlab.client.discord.client.DiscordWebhookClient
import com.hero.alignlab.client.discord.client.SuspendableDiscordWebhookClient
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
class DiscordWebhookClientConfig {
    private val logger = KotlinLogging.logger { }

    @Bean
    @ConditionalOnProperty(prefix = "client.discord.webhook", name = ["url"])
    @ConfigurationProperties(prefix = "client.discord.webhook")
    fun discordWebhookConfig() = Config()

    @Bean
    @ConditionalOnBean(name = ["discordWebhookConfig"])
    @ConditionalOnMissingBean(DiscordWebhookClient::class)
    fun discordWebhookClient(
        @Valid discordWebhookConfig: Config
    ): DiscordWebhookClient {
        logger.info { "initialized DiscordWebhookClient. $discordWebhookConfig" }

        val webclient = WebClientFactory.generate(discordWebhookConfig.url)

        return SuspendableDiscordWebhookClient(webclient, discordWebhookConfig.channels)
    }

    data class Config(
        val url: String = "https://discord.com/api/webhooks",
        var channels: Map<Channel, Token> = emptyMap()
    ) {
        data class Token(
            @field:NotBlank
            var token: String = "",
        )

        enum class Channel {
            STATISTICS, DISCUSSION;
        }
    }
}
