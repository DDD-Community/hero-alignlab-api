package com.hero.alignlab.client.discord.client

import com.hero.alignlab.client.discord.config.DiscordWebhookClientConfig
import com.hero.alignlab.client.discord.model.request.SendMessageRequest
import com.hero.alignlab.client.discord.model.response.GetWebhookWithTokenResponse

interface DiscordWebhookClient {
    suspend fun getWebhookWithToken(channel: DiscordWebhookClientConfig.Config.Channel): GetWebhookWithTokenResponse

    suspend fun sendMessage(channel: DiscordWebhookClientConfig.Config.Channel, request: SendMessageRequest)
}
