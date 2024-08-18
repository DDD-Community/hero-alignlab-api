package com.hero.alignlab.client.discord.client

import com.hero.alignlab.client.discord.model.request.SendMessageRequest
import com.hero.alignlab.client.discord.model.response.GetWebhookWithTokenResponse
import com.hero.alignlab.client.kakao.SuspendableClient
import org.springframework.web.reactive.function.client.WebClient

class SuspendableDiscordWebhookClient(
    client: WebClient,
) : DiscordWebhookClient, SuspendableClient(client) {
    override suspend fun getWebhookWithToken(): GetWebhookWithTokenResponse {
        return client
            .get()
            .request()
    }

    override suspend fun sendMessage(request: SendMessageRequest) {
        return client
            .post()
            .bodyValue(request)
            .request()
    }
}
