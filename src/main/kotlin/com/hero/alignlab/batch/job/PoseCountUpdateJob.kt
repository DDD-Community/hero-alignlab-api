package com.hero.alignlab.batch.job

import com.hero.alignlab.domain.pose.application.PoseCountService
import com.hero.alignlab.domain.pose.application.PoseSnapshotService
import org.joda.time.LocalDate
import org.springframework.stereotype.Component

@Component
class PoseCountUpdateJob(
    private val poseCountService: PoseCountService,
    private val poseSnapshotService: PoseSnapshotService,
) {
    /**
     * **전날 종합 데이터 업데이트**
     * - 전날 데이터의 싱크가 틀린 경우가 발생했을 때를 대비하여, 새벽에 이전 데이터 정합도를 다시 한번 체크한다.
     */
    suspend fun run() {
        val targetDate = LocalDate.now().minusDays(1)
    }
}
