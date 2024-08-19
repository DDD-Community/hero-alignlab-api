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
            /** 그룹 */
            val groupCountByCreatedAt = async(Dispatchers.IO) {
                groupRepository.countByCreatedAtBetween(fromDate, toDate)
            }
            val groupTotalCount = async(Dispatchers.IO) {
                groupRepository.count()
            }
            val groupUserCountByCreatedAt = async(Dispatchers.IO) {
                groupUserRepository.countByCreatedAtBetween(fromDate, toDate)
            }
            val groupUserTotalCount = async(Dispatchers.IO) {
                groupUserRepository.count()
            }

            /** 문의하기 */
            val discussionCountByCreatedAt = async(Dispatchers.IO) {
                discussionRepository.countByCreatedAtBetween(fromDate, toDate)
            }
            val discussionTotalCount = async(Dispatchers.IO) {
                discussionRepository.count()
            }

            /** syslog */
            val syslogCountByCreatedAt = async(Dispatchers.IO) {
                systemActionLogRepository.countByCreatedAtBetween(fromDate, toDate)
            }
            val syslogTotalCount = async(Dispatchers.IO) {
                systemActionLogRepository.count()
            }

            /** 포즈 */
            val poseNotificationCountByCreatedAt = async(Dispatchers.IO) {
                poseNotificationRepository.countByCreatedAtBetween(fromDate, toDate)
            }
            val poseNotificationTotalCount = async(Dispatchers.IO) {
                poseNotificationRepository.count()
            }
            val poseSnapshotCountByCreatedAt = async(Dispatchers.IO) {
                poseSnapshotRepository.countByCreatedAtBetween(fromDate, toDate)
            }
            val poseSnapshotTotalCount = async(Dispatchers.IO) {
                poseSnapshotRepository.count()
            }

            /** 회원 */
            val credentialUserInfoCountByCreatedAt = async(Dispatchers.IO) {
                credentialUserInfoRepository.countByCreatedAtBetween(fromDate, toDate)
            }
            val credentialUserInfoCountTotalCount = async(Dispatchers.IO) {
                credentialUserInfoRepository.count()
            }
            val oAuthUserInfoCountByCreatedAt = async(Dispatchers.IO) {
                oAuthUserInfoRepository.countByCreatedAtBetween(fromDate, toDate)
            }
            val oAuthUserInfoTotalCount = async(Dispatchers.IO) {
                oAuthUserInfoRepository.count()
            }
            val userInfoCountByCreatedAt = async(Dispatchers.IO) {
                userInfoRepository.countByCreatedAtBetween(fromDate, toDate)
            }
            val userInfoTotalCount = async(Dispatchers.IO) {
                userInfoRepository.count()
            }

            /** 이미지 */
            val imageCountByCreatedAt = async(Dispatchers.IO) {
                imageRepository.countByCreatedAtBetween(fromDate, toDate)
            }
            val imageTotalCount = async(Dispatchers.IO) {
                imageRepository.count()
            }

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

            val message = """
                $title [${fromDate.format(formatter)} ~ ${toDate.format(formatter)}]
                
                그룹
                - 그룹 생성수 : ${groupCountByCreatedAt.await()}건 [총합: ${groupTotalCount.await()}건]
                - 그룹 유저 생성수 : ${groupUserCountByCreatedAt.await()}건 [총합: ${groupUserTotalCount.await()}건]
                
                문의하기
                - 문의하기 생성수 : ${discussionCountByCreatedAt.await()}건 [총합: ${discussionTotalCount.await()}건]
                
                API
                - api 호출량 : ${syslogCountByCreatedAt.await()}건 [총합: ${syslogTotalCount.await()}건]
                
                포즈
                - 포즈 알림 설정수 : ${poseNotificationCountByCreatedAt.await()}건 [총합: ${poseNotificationTotalCount.await()}건]
                - 포즈 스냅샷 생성수 : ${poseSnapshotCountByCreatedAt.await()}건 [총합: ${poseSnapshotTotalCount.await()}건]
                
                회원
                - 일반 회원가입수 : ${credentialUserInfoCountByCreatedAt.await()}건 [총합: ${credentialUserInfoCountTotalCount.await()}건]
                - OAuth 회원가입수 : ${oAuthUserInfoCountByCreatedAt.await()}건 [총합: ${oAuthUserInfoTotalCount.await()}건]
                - 유저 생성수 : ${userInfoCountByCreatedAt.await()}건 [총합: ${userInfoTotalCount.await()}건]
                
                이미지
                - 이미지 생성수 : ${imageCountByCreatedAt.await()}건 [총합: ${imageTotalCount.await()}건]
            """.trimIndent()

            discordWebhookService.sendMessage(SendMessageRequest(message))
        }
    }
}
