package com.hero.alignlab.event.listener

import com.hero.alignlab.common.extension.executesOrNull
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.pose.application.PoseKeyPointSnapshotService
import com.hero.alignlab.domain.pose.domain.PoseKeyPointSnapshot
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

            txTemplates.writer.executesOrNull {
                poseKeyPointSnapshotService.saveAllSync(keyPoints)
            }
        }
    }
}
