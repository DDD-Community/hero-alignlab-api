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
                    y = keyPoint.y,
                    x = keyPoint.x,
                    confidence = keyPoint.confidence
                )
            }

            val targetDate = event.poseSnapshot.createdAt.toLocalDate()

            /** 집계 데이터 처리 */
            val poseCount = targetDate
                .run { poseCountService.findByUidAndDateOrNull(event.poseSnapshot.uid, this) }
                ?: PoseCount(uid = event.poseSnapshot.uid, date = targetDate)

            val updatedPoseCount = poseCount.apply {
                val typeCount = this.totalCount.count[event.poseSnapshot.type] ?: 0
                this.totalCount.count[event.poseSnapshot.type] = typeCount + 1
            }

            val score = updatedPoseCount.totalCount.count
                .filter { (key, _) -> key in BAD_POSE }
                .values
                .sum()

            /** group score 처리 */
            val groupUser = groupUserService.findByUid(event.poseSnapshot.uid)
            val updatedGroupUserScore = when (groupUser == null) {
                true -> null
                false -> {
                    val groupUserScore = groupUserScoreService.findByUidOrNull(event.poseSnapshot.uid)

                    when (groupUserScore != null) {
                        true -> groupUserScore.apply {
                            this.score = score
                        }

                        false -> GroupUserScore(
                            groupId = groupUser.groupId,
                            groupUserId = groupUser.id,
                            uid = groupUser.uid,
                            score = score
                        )
                    }
                }
            }

            txTemplates.writer.coExecuteOrNull {
                poseKeyPointSnapshotService.saveAllSync(keyPoints)
                poseCountService.saveSync(updatedPoseCount)
                updatedGroupUserScore?.run { groupUserScoreService.saveSync(this) }
            }
        }
    }
}
