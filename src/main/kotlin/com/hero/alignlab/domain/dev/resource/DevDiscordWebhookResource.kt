package com.hero.alignlab.domain.dev.resource

import com.hero.alignlab.batch.statistics.job.HeroStatisticsJob
import com.hero.alignlab.client.discord.client.DiscordWebhookClient
import com.hero.alignlab.client.discord.model.request.SendMessageRequest
import com.hero.alignlab.config.dev.DevResourceCheckConfig.Companion.devResource
import com.hero.alignlab.config.swagger.SwaggerTag.DEV_TAG
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@Tag(name = DEV_TAG)
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class DevDiscordWebhookResource(
    private val discordWebhookClient: DiscordWebhookClient,
    private val statisticsJob: HeroStatisticsJob,
) {
    @Operation(summary = "discord webhook test")
    @PostMapping("/api/dev/v1/discord-webhooks/{id}")
    suspend fun sendMessage(
        @RequestHeader("X-HERO-DEV-TOKEN") token: String,
        @RequestParam message: String,
    ) = devResource(token) {
        discordWebhookClient.sendMessage(SendMessageRequest(message))
    }

    @Operation(summary = "discord webhook daily noti")
    @PostMapping("/api/dev/v1/discord-webhooks/daily-noti")
    suspend fun sendDailyNoti(
        @RequestHeader("X-HERO-DEV-TOKEN") token: String,
    ) = devResource(token) {
        val toDate = LocalDateTime.now()
        val fromDate = LocalDateTime.now().minusHours(1)

        statisticsJob.sendHeroStatistics("극락통계 테스트", fromDate, toDate)
    }
}
