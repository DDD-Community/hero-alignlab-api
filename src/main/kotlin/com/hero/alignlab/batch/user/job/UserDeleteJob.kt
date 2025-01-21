package com.hero.alignlab.batch.user.job

import com.hero.alignlab.domain.cheer.infrastructure.CheerUpRepository
import com.hero.alignlab.domain.discussion.infrastructure.DiscussionCommentRepository
import com.hero.alignlab.domain.discussion.infrastructure.DiscussionRepository
import com.hero.alignlab.domain.group.infrastructure.GroupRepository
import com.hero.alignlab.domain.group.infrastructure.GroupUserRepository
import com.hero.alignlab.domain.group.infrastructure.GroupUserScoreRepository
import com.hero.alignlab.domain.image.infrastructure.ImageMetadataRepository
import com.hero.alignlab.domain.log.infrastructure.SystemActionLogRepository
import com.hero.alignlab.domain.notification.infrastructure.PoseNotificationRepository
import com.hero.alignlab.domain.pose.infrastructure.*
import com.hero.alignlab.domain.user.infrastructure.CredentialUserInfoRepository
import com.hero.alignlab.domain.user.infrastructure.OAuthUserInfoRepository
import com.hero.alignlab.domain.user.infrastructure.UserInfoRepository
import org.springframework.stereotype.Component

@Component
class UserDeleteJob(
    /** user 정보 */
    private val userInfoRepository: UserInfoRepository,
    private val credentialUserInfoRepository: CredentialUserInfoRepository,
    private val oauthUserInfoRepository: OAuthUserInfoRepository,

    /** post 정보 */
    private val poseLayoutRepository: PoseLayoutRepository,
    private val poseLayoutPointRepository: PoseLayoutPointRepository,
    private val poseCountRepository: PoseCountRepository,
    private val poseKeyPointSnapshotRepository: PoseKeyPointSnapshotRepository,
    private val poseSnapshotRepository: PoseSnapshotRepository,

    /** noti */
    private val poseNotificationRepository: PoseNotificationRepository,

    /** syslog */
    private val systemActionLogRepository: SystemActionLogRepository,

    /** image metadata */
    private val imageMetadataRepository: ImageMetadataRepository,

    /** group */
    private val groupRepository: GroupRepository,
    private val groupUserRepository: GroupUserRepository,
    private val groupUserScoreRepository: GroupUserScoreRepository,

    /** discussion */
    private val discussionRepository: DiscussionRepository,
    private val discussionCommentRepository: DiscussionCommentRepository,

    /** cheer-up */
    private val cheerUpRepository: CheerUpRepository,
) {
    fun delete() {
        /** 전체 유저 정보 조회, uids가 비여있으면 종료시킴 -> *만약 이 조건 풀어버리면, 전부 삭제가 진행.* */
        val uids = userInfoRepository.findAll().map { it.id }
            .takeIf { it.isNotEmpty() } ?: return

        /** 유저 관련 데이터 삭제 */
        credentialUserInfoRepository.deleteAllByUidNotIn(uids)
        oauthUserInfoRepository.deleteAllByUidNotIn(uids)

        /** pose 데이터 삭제 */
        val poseLayoutIds = poseLayoutRepository.findAllByUidNotIn(uids).map { it.id }
        poseLayoutRepository.deleteAllByUidNotIn(uids)
        poseLayoutPointRepository.deleteAllByPoseLayoutIdIn(poseLayoutIds)
        poseCountRepository.deleteAllByUidNotIn(uids)
        val poseSnapshotIds = poseSnapshotRepository.findAllByUidNotIn(uids)
            .map { it.id }
        poseSnapshotRepository.deleteAllByUidNotIn(uids)
        poseKeyPointSnapshotRepository.deleteAllByPoseSnapshotIdIn(poseSnapshotIds)
        poseNotificationRepository.deleteAllByUidNotIn(uids)

        /** sys log 삭제 */
        systemActionLogRepository.deleteAllByUidNotIn(uids)

        /** img 삭제 */
        imageMetadataRepository.deleteAllByUidNotIn(uids)

        /** group user 삭제 */
        groupUserRepository.deleteAllByUidNotIn(uids)
        groupUserScoreRepository.deleteAllByUidNotIn(uids)

        /** discussion */
        discussionRepository.deleteAllByUidNotIn(uids)
        discussionCommentRepository.deleteAllByUidNotIn(uids)

        /** cheer-up */
        cheerUpRepository.deleteAllByUidNotIn(uids)
    }
}
