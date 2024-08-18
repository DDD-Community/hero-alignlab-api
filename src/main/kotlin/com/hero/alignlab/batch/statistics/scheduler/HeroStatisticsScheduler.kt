package com.hero.alignlab.batch.statistics.scheduler

import com.hero.alignlab.batch.statistics.job.HeroStatisticsJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class HeroStatisticsScheduler(
    private val heroStatisticsJob: HeroStatisticsJob
) {
    @Scheduled(cron = "0 0 0/1 * * *")
    fun dailyRunJob() {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            val toDate = LocalDateTime.now()
            val fromDate = LocalDateTime.now().minusHours(1)

            heroStatisticsJob.sendHeroStatistics(
                title = "극락통계 1시간 단위",
                fromDate = fromDate,
                toDate = toDate
            )
        }
    }

    @Scheduled(cron = "0 0 9 * * *")
    fun runDailySummary() {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            val toDate = LocalDateTime.now()
            val fromDate = LocalDateTime.now().minusDays(1)

            heroStatisticsJob.sendHeroStatistics(
                title = "극락통계 1일 단위",
                fromDate = fromDate,
                toDate = toDate
            )
        }
    }
}
