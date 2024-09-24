package com.hero.alignlab.client.discord.client

import com.hero.alignlab.client.discord.config.DiscordWebhookClientConfig
import com.hero.alignlab.client.discord.config.DiscordWebhookClientConfig.Config.Channel
import com.hero.alignlab.client.discord.model.request.SendMessageRequest
import com.hero.alignlab.client.discord.model.response.GetWebhookWithTokenResponse
import com.hero.alignlab.client.kakao.SuspendableClient
import org.springframework.web.reactive.function.client.WebClient

class SuspendableDiscordWebhookClient(
    client: WebClient,
    private val config: DiscordWebhookClientConfig.Config,
) : DiscordWebhookClient, SuspendableClient(client) {
    override suspend fun getWebhookWithToken(channel: Channel): GetWebhookWithTokenResponse {
        return client
            .get()
            .uri(config.resolveUrl(channel))
            .request()
    }

    override suspend fun sendMessage(channel: Channel, request: SendMessageRequest) {
        return client
            .post()
            .uri(config.resolveUrl(channel))
            .bodyValue(request)
            .request()
    }
}
