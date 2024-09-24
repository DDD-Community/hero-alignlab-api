package com.hero.alignlab.client.discord.client

import com.hero.alignlab.client.discord.config.DiscordWebhookClientConfig.Config.Channel
import com.hero.alignlab.client.discord.config.DiscordWebhookClientConfig.Config.Token
import com.hero.alignlab.client.discord.model.request.SendMessageRequest
import com.hero.alignlab.client.discord.model.response.GetWebhookWithTokenResponse
import com.hero.alignlab.client.kakao.SuspendableClient
import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.NotFoundException
import org.springframework.web.reactive.function.client.WebClient

class SuspendableDiscordWebhookClient(
    client: WebClient,
    private val targets: Map<Channel, Token>,
) : DiscordWebhookClient, SuspendableClient(client) {
    override suspend fun getWebhookWithToken(channel: Channel): GetWebhookWithTokenResponse {
        return client
            .get()
            .uri(resolveTargetToken(channel))
            .request()
    }

    override suspend fun sendMessage(channel: Channel, request: SendMessageRequest) {
        return client
            .post()
            .uri(resolveTargetToken(channel))
            .bodyValue(request)
            .request()
    }

    private fun resolveTargetToken(channel: Channel): String {
        return targets[channel]?.token ?: throw NotFoundException(ErrorCode.NOT_FOUND_TARGET_TOKEN)
    }
}
