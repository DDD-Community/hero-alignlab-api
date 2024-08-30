package com.hero.alignlab.batch.posecount.job

import com.hero.alignlab.common.extension.executesOrNull
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.pose.application.PoseCountService
import com.hero.alignlab.domain.pose.application.PoseSnapshotService
import com.hero.alignlab.domain.pose.domain.vo.PoseTotalCount
import com.hero.alignlab.domain.user.application.UserInfoService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class PoseCountUpdateJob(
    private val poseCountService: PoseCountService,
    private val poseSnapshotService: PoseSnapshotService,
    private val userInfoService: UserInfoService,
    private val txTemplates: TransactionTemplates,
) {
    private val logger = KotlinLogging.logger { }

    /**
     * **전날 종합 데이터 업데이트**
     * - 전날 데이터의 싱크가 틀린 경우가 발생했을 때를 대비하여, 새벽에 이전 데이터 정합도를 다시 한번 체크한다.
     */
    suspend fun run(targetDate: LocalDate) {
        logger.info { "start PoseCountUpdateJob.run()" }

        /** user의 수가 적으므로, 전체 uid를 조회, 만약에 oom이 터진다면, 서비스 대박임 */
        val uids = userInfoService.findAllUids()

        uids
            .chunked(20)
            .forEach { targetUids ->
                /** 전날 카운트 집계 정보를 전체 조회 */
                val totalCountByUid = poseSnapshotService.countByUidsAndDate(targetUids, targetDate)
                    .groupBy { totalCount -> totalCount.uid }

                /** 집계 처리 데이터 조회 */
                val poseCounts = poseCountService.findAllByUidIn(targetUids)
                    .mapNotNull { poseCount ->
                        /** 데이터가 없는 경우, 업데이트를 진행하지 않는다. */
                        val totalCount = totalCountByUid[poseCount.uid] ?: return@mapNotNull null

                        /** 통계 데이터 업데이트 */
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

        logger.info { "finished PoseCountUpdateJob.run()" }
    }
}
