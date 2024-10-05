package com.hero.alignlab.batch.user.job

import com.hero.alignlab.domain.log.application.SystemActionLogService
import com.hero.alignlab.domain.user.application.UserInfoService
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class UserLevelJob(
    private val systemActionLogService: SystemActionLogService,
    private val userInfoService: UserInfoService,
) {
    suspend fun run() {
        val toDate = LocalDateTime.now()
        val fromDate = toDate.minusDays(1)

        val activeUsers = systemActionLogService.countActiveUser(fromDate, toDate, 1000)
            .filter { user -> user.count > 100 }
            .map { user -> user.uid }

        val users = userInfoService.findAllById(activeUsers)
            .mapNotNull { user ->
                if (user.maxLevel) {
                    return@mapNotNull null
                }

                user.apply { this.level += 1 }
            }

        userInfoService.saveAll(users)
    }
}
