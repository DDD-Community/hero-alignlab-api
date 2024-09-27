package com.hero.alignlab.event.listener

import com.hero.alignlab.common.extension.coExecuteOrNull
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.group.application.GroupFacade
import com.hero.alignlab.domain.pose.application.PoseCountService
import com.hero.alignlab.domain.pose.application.PoseKeyPointSnapshotService
import com.hero.alignlab.domain.pose.application.PoseSnapshotService
import com.hero.alignlab.domain.pose.domain.PoseCount
import com.hero.alignlab.domain.pose.domain.PoseKeyPointSnapshot
import com.hero.alignlab.domain.pose.domain.vo.PoseTotalCount
import com.hero.alignlab.event.model.LoadPoseSnapshot
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
    private val groupFacade: GroupFacade,
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

            val poseSnapshot = poseSnapshotService.countByUidsAndDate(listOf(event.poseSnapshot.uid), targetDate)
                .associate { snapshot -> snapshot.type to snapshot.count.toInt() }

            /** 집계 데이터 처리 */
            val poseCount = poseCountService.findByUidAndDateOrNull(event.poseSnapshot.uid, targetDate)
                ?: PoseCount(uid = event.poseSnapshot.uid, date = targetDate)

            /** 집계 데이터 다시 조회하여, 동기화 진행 */
            val updatedPoseCount = poseCount.apply {
                this.totalCount = PoseTotalCount(count = poseSnapshot)
            }

            /** 포즈에 연관된 데이터 처리 */
            txTemplates.writer.coExecuteOrNull {
                poseKeyPointSnapshotService.saveAllSync(keyPoints)
                poseCountService.saveSync(updatedPoseCount)
            }

            groupFacade.refreshGroupScore(event.poseSnapshot.uid)
        }
    }
}
