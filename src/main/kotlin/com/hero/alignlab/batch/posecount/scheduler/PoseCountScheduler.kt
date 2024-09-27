package com.hero.alignlab.batch.posecount.scheduler

import com.hero.alignlab.batch.posecount.job.PoseCountUpdateJob
import com.hero.alignlab.common.extension.CoroutineExtension.retryOnError
import com.hero.alignlab.common.extension.resolveCancellation
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class PoseCountScheduler(
    private val poseCountUpdateJob: PoseCountUpdateJob
) {
    private val logger = KotlinLogging.logger { }

    /** 전일 데이터에 대한 정합성 체크 */
    // @Scheduled(cron = "0 0 4 * * *")
    fun poseCountUpdateJob() {
        val targetDate = LocalDate.now().minusDays(1)

        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    retryOnError(2) {
                        poseCountUpdateJob.run(targetDate)
                    }
                }
            }.onFailure { e ->
                logger.resolveCancellation("poseCountUpdateJob targetDate: $targetDate", e)
            }.getOrThrow()
        }
    }
}
