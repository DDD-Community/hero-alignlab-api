package com.hero.alignlab.batch.statistics.job

import com.hero.alignlab.client.discord.DiscordWebhookService
import com.hero.alignlab.client.discord.model.request.SendMessageRequest
import com.hero.alignlab.domain.discussion.infrastructure.DiscussionRepository
import com.hero.alignlab.domain.group.infrastructure.GroupRepository
import com.hero.alignlab.domain.group.infrastructure.GroupUserRepository
import com.hero.alignlab.domain.log.infrastructure.SystemActionLogRepository
import com.hero.alignlab.domain.notification.infrastructure.PoseNotificationRepository
import com.hero.alignlab.domain.pose.infrastructure.PoseSnapshotRepository
import com.hero.alignlab.domain.user.infrastructure.CredentialUserInfoRepository
import com.hero.alignlab.domain.user.infrastructure.OAuthUserInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class HeroStatisticsJob(
    private val discordWebhookService: DiscordWebhookService,

    /** repository */
    private val groupRepository: GroupRepository,
    private val groupUserRepository: GroupUserRepository,
    private val discussionRepository: DiscussionRepository,
    private val imageRepository: GroupRepository,
    private val systemActionLogRepository: SystemActionLogRepository,
    private val poseNotificationRepository: PoseNotificationRepository,
    private val poseSnapshotRepository: PoseSnapshotRepository,
    private val credentialUserInfoRepository: CredentialUserInfoRepository,
    private val oAuthUserInfoRepository: OAuthUserInfoRepository,
    private val userInfoRepository: CredentialUserInfoRepository,
) {
    suspend fun sendHeroStatistics(title: String, fromDate: LocalDateTime, toDate: LocalDateTime) {
        coroutineScope {
            val groupCount = async(Dispatchers.IO) {
                groupRepository.countByCreatedAtBetween(fromDate, toDate)
            }.await()
            val groupUserCount = async(Dispatchers.IO) {
                groupUserRepository.countByCreatedAtBetween(fromDate, toDate)
            }.await()
            val discussionCount = async(Dispatchers.IO) {
                discussionRepository.countByCreatedAtBetween(fromDate, toDate)
            }.await()
            val syslogCount = async(Dispatchers.IO) {
                systemActionLogRepository.countByCreatedAtBetween(fromDate, toDate)
            }.await()
            val poseNotificationCount = async(Dispatchers.IO) {
                poseNotificationRepository.countByCreatedAtBetween(fromDate, toDate)
            }.await()
            val poseSnapshotCount = async(Dispatchers.IO) {
                poseSnapshotRepository.countByCreatedAtBetween(fromDate, toDate)
            }.await()
            val credentialUserInfoCount = async(Dispatchers.IO) {
                credentialUserInfoRepository.countByCreatedAtBetween(fromDate, toDate)
            }.await()
            val oAuthUserInfoCount = async(Dispatchers.IO) {
                oAuthUserInfoRepository.countByCreatedAtBetween(fromDate, toDate)
            }.await()
            val userInfoCount = async(Dispatchers.IO) {
                userInfoRepository.countByCreatedAtBetween(fromDate, toDate)
            }.await()
            val imageCount = async(Dispatchers.IO) {
                imageRepository.countByCreatedAtBetween(fromDate, toDate)
            }.await()

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

            val message = """
                $title
                - targetDate: ${fromDate.format(formatter)} ~ ${toDate.format(formatter)}
                - 그룹 생성수 : $groupCount
                - 그룹 유저 생성수 : $groupUserCount
                - 문의하기 생성수 : $discussionCount
                - api 호출량 : $syslogCount
                - 포즈 알림 설정수 : $poseNotificationCount
                - 포즈 스냅샷 생성수 : $poseSnapshotCount
                - 일반 회원가입수 : $credentialUserInfoCount
                - OAuth 회원가입수 : $oAuthUserInfoCount
                - 유저 생성수 : $userInfoCount
                - 이미지 생성수 : $imageCount
            """.trimIndent()

            discordWebhookService.sendMessage(SendMessageRequest(message))
        }
    }
}
