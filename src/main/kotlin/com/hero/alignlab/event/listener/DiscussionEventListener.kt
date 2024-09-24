package com.hero.alignlab.event.listener

import com.hero.alignlab.client.discord.client.DiscordWebhookClient
import com.hero.alignlab.client.discord.config.DiscordWebhookClientConfig
import com.hero.alignlab.client.discord.model.request.SendMessageRequest
import com.hero.alignlab.domain.user.application.UserInfoService
import com.hero.alignlab.event.model.DiscussionEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class DiscussionEventListener(
    private val discordWebhookClient: DiscordWebhookClient,
    private val userInfoService: UserInfoService,
) {
    @TransactionalEventListener
    fun handle(event: DiscussionEvent) {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

            val userInfo = userInfoService.getUserInfo(event.discussion.uid)

            val message = """
                **문의하기 [${LocalDateTime.now().format(formatter)}]**
                
                - 유저명 : ${userInfo.nickname}
                - 이메일 : ${event.discussion.email ?: "없음"}
                - type : ${event.discussion.type}
                - title : ${event.discussion.title}
                - content : ${event.discussion.content}
            """.trimIndent()

            discordWebhookClient.sendMessage(
                channel = DiscordWebhookClientConfig.Config.Channel.DISCUSSION,
                request = SendMessageRequest(message)
            )
        }
    }
}
