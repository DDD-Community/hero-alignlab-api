package com.hero.alignlab.batch.user.scheduler

import com.hero.alignlab.batch.user.job.UserLevelJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class UserLevelScheduler(
    private val userLevelJob: UserLevelJob,
) {
    @Scheduled(cron = "0 0 * * * *")
    fun run() {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            userLevelJob.run()
        }
    }
}
