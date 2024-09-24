package com.hero.alignlab.domain.dev.resource

import com.hero.alignlab.batch.statistics.job.HeroStatisticsJob
import com.hero.alignlab.client.discord.client.DiscordWebhookClient
import com.hero.alignlab.client.discord.config.DiscordWebhookClientConfig
import com.hero.alignlab.client.discord.model.request.SendMessageRequest
import com.hero.alignlab.config.swagger.SwaggerTag.DEV_TAG
import com.hero.alignlab.domain.auth.model.DevAuthUser
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
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
        dev: DevAuthUser,
        @RequestParam channel: DiscordWebhookClientConfig.Config.Channel,
        @RequestParam message: String,
    ) {
        discordWebhookClient.sendMessage(channel, SendMessageRequest(message))
    }

    @Operation(summary = "discord webhook daily noti")
    @PostMapping("/api/dev/v1/discord-webhooks/daily-noti")
    suspend fun sendDailyNoti(
        dev: DevAuthUser,
        @RequestParam fromDateTime: LocalDateTime,
        @RequestParam toDateTime: LocalDateTime,
    ) {
        statisticsJob.sendHeroStatistics(
            title = "극락통계 테스트",
            fromDate = fromDateTime,
            toDate = toDateTime
        )
    }
}
