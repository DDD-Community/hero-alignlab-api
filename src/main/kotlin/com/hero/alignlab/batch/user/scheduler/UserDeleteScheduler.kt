package com.hero.alignlab.batch.user.scheduler

import com.hero.alignlab.batch.user.job.UserDeleteJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class UserDeleteScheduler(
    private val userDeleteJob: UserDeleteJob
) {
    @Scheduled(cron = "0 0 4 * * *")
    fun run() {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            userDeleteJob.delete()
        }
    }
}
