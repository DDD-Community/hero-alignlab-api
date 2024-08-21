package com.hero.alignlab.event.listener

import com.hero.alignlab.common.extension.coExecuteOrNull
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.group.application.GroupUserScoreService
import com.hero.alignlab.domain.group.application.GroupUserService
import com.hero.alignlab.domain.group.domain.GroupUserScore
import com.hero.alignlab.domain.pose.application.PoseCountService
import com.hero.alignlab.domain.pose.application.PoseKeyPointSnapshotService
import com.hero.alignlab.domain.pose.domain.PoseCount
import com.hero.alignlab.domain.pose.domain.PoseKeyPointSnapshot
import com.hero.alignlab.domain.pose.domain.vo.PoseTotalCount
import com.hero.alignlab.domain.pose.domain.vo.PoseType.Companion.BAD_POSE
import com.hero.alignlab.event.model.LoadPoseSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class PoseSnapshotListener(
    private val poseKeyPointSnapshotService: PoseKeyPointSnapshotService,
    private val poseCountService: PoseCountService,
    private val groupUserScoreService: GroupUserScoreService,
    private val groupUserService: GroupUserService,
    private val txTemplates: TransactionTemplates,
) {
    @TransactionalEventListener
    fun handle(event: LoadPoseSnapshot) {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            val keyPoints = event.keyPoints.map { keyPoint ->
                PoseKeyPointSnapshot(
                    poseSnapshotId = event.poseSnapshot.id,
                    position = keyPoint.name.toPosition(),
                    x = keyPoint.x,
                    y = keyPoint.y,
                    confidence = keyPoint.confidence
                )
            }

            val targetDate = event.poseSnapshot.createdAt.toLocalDate()

            /** 집계 데이터 처리 */
            val poseCount = targetDate
                .run { poseCountService.findByUidAndDateOrNull(event.poseSnapshot.uid, this) }
                ?.apply {
                    val typeCount = this.totalCount.count[event.poseSnapshot.type] ?: 0
                    this.totalCount.count[event.poseSnapshot.type] = typeCount + 1
                } ?: PoseCount(
                uid = event.poseSnapshot.uid,
                totalCount = PoseTotalCount(
                    count = mutableMapOf(
                        event.poseSnapshot.type to 1
                    )
                ),
                date = targetDate
            )

            val score = poseCount.totalCount.count
                .filter { (key, _) -> key in BAD_POSE }
                .values
                .sum()

            val groupUsers = groupUserService.findAllByUid(event.poseSnapshot.uid)
            val groupUserScore = groupUserScoreService.findAllByGroupUserIdIn(groupUsers.map { it.id })
                .associateBy { it.groupUserId }

            val needToUpdateScores = groupUsers.map { groupUser ->
                groupUserScore[groupUser.id] ?: GroupUserScore(
                    groupId = groupUser.groupId,
                    groupUserId = groupUser.id,
                    uid = groupUser.uid,
                    score = score
                )
            }

            txTemplates.writer.coExecuteOrNull {
                poseKeyPointSnapshotService.bulkSave(keyPoints)
                poseCountService.saveSync(poseCount)
                groupUserScoreService.saveAllSync(needToUpdateScores)
            }
        }
    }
}
