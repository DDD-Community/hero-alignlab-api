package com.hero.alignlab.client.discord.client

import com.hero.alignlab.client.discord.model.request.SendMessageRequest
import com.hero.alignlab.client.discord.model.response.GetWebhookWithTokenResponse

interface DiscordWebhookClient {
    suspend fun getWebhookWithToken(): GetWebhookWithTokenResponse

    suspend fun sendMessage(request: SendMessageRequest)
}
