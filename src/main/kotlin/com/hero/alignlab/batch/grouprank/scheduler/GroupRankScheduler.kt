package com.hero.alignlab.batch.grouprank.scheduler

import com.hero.alignlab.batch.grouprank.job.GroupRankRefreshJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class GroupRankScheduler(
    private val groupRankRefreshJob: GroupRankRefreshJob,
) {
    /** 3초에 한번 스케줄러 동작 */
    @Scheduled(fixedRate = 3000)
    fun runRefreshGroupRank() {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            groupRankRefreshJob.run()
        }
    }
}
