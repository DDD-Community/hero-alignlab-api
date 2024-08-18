package com.hero.alignlab.client.discord

import com.hero.alignlab.client.discord.client.DiscordWebhookClient
import com.hero.alignlab.client.discord.model.request.SendMessageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class DiscordWebhookService(
    private val discordWebhookClient: DiscordWebhookClient,
) {
    suspend fun sendMessage(request: SendMessageRequest) {
        withContext(Dispatchers.IO) {
            discordWebhookClient.sendMessage(request)
        }
    }
}
