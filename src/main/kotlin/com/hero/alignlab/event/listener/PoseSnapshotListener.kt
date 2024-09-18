package com.hero.alignlab.event.listener

import com.hero.alignlab.common.extension.coExecuteOrNull
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.group.application.GroupUserScoreService
import com.hero.alignlab.domain.group.application.GroupUserService
import com.hero.alignlab.domain.pose.application.PoseCountService
import com.hero.alignlab.domain.pose.application.PoseKeyPointSnapshotService
import com.hero.alignlab.domain.pose.application.PoseSnapshotService
import com.hero.alignlab.domain.pose.domain.PoseCount
import com.hero.alignlab.domain.pose.domain.PoseKeyPointSnapshot
import com.hero.alignlab.domain.pose.domain.vo.PoseType.Companion.BAD_POSE
import com.hero.alignlab.event.model.LoadPoseSnapshot
import com.hero.alignlab.ws.handler.ReactiveGroupUserWebSocketHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class PoseSnapshotListener(
    private val poseSnapshotService: PoseSnapshotService,
    private val poseKeyPointSnapshotService: PoseKeyPointSnapshotService,
    private val poseCountService: PoseCountService,
    private val groupUserScoreService: GroupUserScoreService,
    private val groupUserService: GroupUserService,
    private val txTemplates: TransactionTemplates,
    private val wsHandler: ReactiveGroupUserWebSocketHandler,
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
            val poseCount = poseCountService.findByUidAndDateOrNull(event.poseSnapshot.uid, targetDate)
                ?: PoseCount(uid = event.poseSnapshot.uid, date = targetDate)

            val updatedPoseCount = poseCount.apply {
                val type = event.poseSnapshot.type

                val typeCount = this.totalCount.count[type] ?: 0
                this.totalCount.count[type] = typeCount + 1
            }

            /** 포즈에 연관된 데이터 처리 */
            txTemplates.writer.coExecuteOrNull {
                poseKeyPointSnapshotService.saveAllSync(keyPoints)
                poseCountService.saveSync(updatedPoseCount)
            }

            /** group score 처리 */
            groupUserService.findByUidOrNull(event.poseSnapshot.uid)?.run {
                val to = event.poseSnapshot.createdAt
                val from = to.minusHours(1)

                val score = poseSnapshotService.countByUidAndModifiedAt(
                    uid = event.poseSnapshot.uid,
                    fromCreatedAt = from,
                    toCreatedAt = to
                ).filter { model -> model.type in BAD_POSE }.sumOf { model -> model.count }.toInt()

                val groupUserScore = groupUserScoreService.createOrUpdateGroupUserScore(this, score)

                wsHandler.launchSendEvent(groupUserScore.uid, groupUserScore.groupId)
            }
        }
    }
}
