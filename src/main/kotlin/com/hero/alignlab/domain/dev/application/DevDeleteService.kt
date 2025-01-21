package com.hero.alignlab.domain.dev.application

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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DevDeleteService(
    /** user 정보 */
    private val credentialUserInfoRepository: CredentialUserInfoRepository,
    private val oauthUserInfoRepository: OAuthUserInfoRepository,
    private val userInfoRepository: UserInfoRepository,

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
    @Transactional
    fun deleteAll(uid: Long) {
        /** 유저 정보 삭제 */
        credentialUserInfoRepository.deleteAllByUid(uid)
        oauthUserInfoRepository.deleteAllByUid(uid)
        userInfoRepository.deleteAllById(listOf(uid))

        /** pose layout 데이터 삭제 */
        val poseLayoutIds = poseLayoutRepository.findAllByUid(uid).map { it.id }

        poseLayoutRepository.deleteAllByUid(uid)
        poseLayoutPointRepository.deleteAllByPoseLayoutIdIn(poseLayoutIds)

        poseCountRepository.deleteAllByUid(uid)
        val poseSnapshotIds = poseSnapshotRepository.findAllByUid(uid)
            .map { it.id }

        poseSnapshotRepository.deleteAllByUid(uid)
        poseKeyPointSnapshotRepository.deleteAllByPoseSnapshotIdIn(poseSnapshotIds)

        /** pose noti 제거 */
        poseNotificationRepository.deleteAllByUid(uid)

        /** syslog */
        systemActionLogRepository.deleteAllByUid(uid)

        /** img */
        imageMetadataRepository.deleteAllByUid(uid)

        /** group */
        groupRepository.deleteAllByOwnerUid(uid)
        groupUserRepository.deleteAllByUid(uid)
        groupUserScoreRepository.deleteAllByUid(uid)

        /** discussion */
        discussionRepository.deleteAllByUid(uid)
        discussionCommentRepository.deleteAllByUid(uid)

        /** cheer-up */
        cheerUpRepository.deleteAllByUid(uid)
    }

    @Transactional
    fun deleteAllWithoutUser(uid: Long) {
        /*
        /** 유저 정보 삭제 */
        credentialUserInfoRepository.deleteAllByUid(uid)
        oauthUserInfoRepository.deleteAllByUid(uid)
        userInfoRepository.deleteById(uid)
        */

        /** pose layout 데이터 삭제 */
        val poseLayoutIds = poseLayoutRepository.findAllByUid(uid).map { it.id }

        poseLayoutRepository.deleteAllByUid(uid)
        poseLayoutPointRepository.deleteAllByPoseLayoutIdIn(poseLayoutIds)

        poseCountRepository.deleteAllByUid(uid)
        val poseSnapshotIds = poseSnapshotRepository.findAllByUid(uid)
            .map { it.id }

        poseSnapshotRepository.deleteAllByUid(uid)
        poseKeyPointSnapshotRepository.deleteAllByPoseSnapshotIdIn(poseSnapshotIds)

        /** pose noti 제거 */
        poseNotificationRepository.deleteAllByUid(uid)

        /** syslog */
        systemActionLogRepository.deleteAllByUid(uid)

        /** img */
        imageMetadataRepository.deleteAllByUid(uid)

        /** group */
        groupRepository.deleteAllByOwnerUid(uid)
        groupUserRepository.deleteAllByUid(uid)
        groupUserScoreRepository.deleteAllByUid(uid)

        /** discussion */
        discussionRepository.deleteAllByUid(uid)
        discussionCommentRepository.deleteAllByUid(uid)
    }
}
