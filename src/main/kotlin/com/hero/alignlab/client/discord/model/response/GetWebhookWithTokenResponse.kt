package com.hero.alignlab.client.discord.model.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GetWebhookWithTokenResponse(
    val applicationId: String? = null,
    val avatar: String? = null,
    val channelId: String? = null,
    val guildId: String? = null,
    val id: String? = null,
    val name: String? = null,
    val type: Int? = null,
    val token: String? = null,
    val url: String? = null
)
