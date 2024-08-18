package com.hero.alignlab.client.discord.client

import com.hero.alignlab.client.discord.config.DiscordWebhookClientConfig
import com.hero.alignlab.client.discord.model.request.SendMessageRequest
import com.hero.alignlab.client.discord.model.response.GetWebhookWithTokenResponse
import com.hero.alignlab.client.kakao.SuspendableClient
import org.springframework.web.reactive.function.client.WebClient

class SuspendableDiscordWebhookClient(
    client: WebClient,
    private val config: DiscordWebhookClientConfig.Config,
) : DiscordWebhookClient, SuspendableClient(client) {
    override suspend fun getWebhookWithToken(id: Int): GetWebhookWithTokenResponse {
        return client
            .get()
            .discordUri(id)
            .request()
    }

    override suspend fun sendMessage(id: Int, request: SendMessageRequest) {
        return client
            .post()
            .discordUri(id)
            .bodyValue(request)
            .request()
    }

    private fun WebClient.RequestHeadersUriSpec<*>.discordUri(id: Int): WebClient.RequestHeadersSpec<*> {
        val credential = getWebhookCredential(id)
        return this.uri("/${credential.webhookId}/${credential.webhookToken}")
    }

    private fun WebClient.RequestBodyUriSpec.discordUri(id: Int): WebClient.RequestBodySpec {
        val credential = getWebhookCredential(id)
        return this.uri("/${credential.webhookId}/${credential.webhookToken}")
    }

    private fun getWebhookCredential(id: Int): DiscordWebhookClientConfig.Config.WebhookContext {
        return config.credential[id] ?: throw RuntimeException("Webhook Credential does not exist, $id")
    }
}
