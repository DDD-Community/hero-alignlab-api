package com.hero.alignlab.client.discord.client

import com.hero.alignlab.client.discord.model.request.SendMessageRequest
import com.hero.alignlab.client.discord.model.response.GetWebhookWithTokenResponse

interface DiscordWebhookClient {
    suspend fun getWebhookWithToken(id: Int): GetWebhookWithTokenResponse

    suspend fun sendMessage(id: Int, request: SendMessageRequest): Void
}
