package com.hero.alignlab.batch.job

import com.hero.alignlab.common.extension.executesOrNull
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.pose.application.PoseCountService
import com.hero.alignlab.domain.pose.application.PoseSnapshotService
import com.hero.alignlab.domain.pose.domain.vo.PoseTotalCount
import com.hero.alignlab.domain.user.application.UserInfoService
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class PoseCountUpdateJob(
    private val poseCountService: PoseCountService,
    private val poseSnapshotService: PoseSnapshotService,
    private val userInfoService: UserInfoService,
    private val txTemplates: TransactionTemplates,
) {
    /**
     * **전날 종합 데이터 업데이트**
     * - 전날 데이터의 싱크가 틀린 경우가 발생했을 때를 대비하여, 새벽에 이전 데이터 정합도를 다시 한번 체크한다.
     */
    suspend fun run() {
        val targetDate = LocalDate.now().minusDays(1)

        val uids = userInfoService.findAllUids()

        uids
            .chunked(20)
            .forEach { targetUids ->
                val totalCountByUid = poseSnapshotService.countByTypeAndDate(targetUids, targetDate)
                    .groupBy { totalCount -> totalCount.uid }

                val poseCounts = poseCountService.findAllByUidIn(targetUids)
                    .mapNotNull { poseCount ->
                        val totalCount = totalCountByUid[poseCount.uid] ?: return@mapNotNull null

                        poseCount.apply {
                            this.totalCount = PoseTotalCount(
                                count = totalCount.associate { it.type to it.count.toInt() }.toMutableMap()
                            )
                        }
                    }

                txTemplates.writer.executesOrNull {
                    poseCountService.saveAllSync(poseCounts)
                }
            }
    }
}
