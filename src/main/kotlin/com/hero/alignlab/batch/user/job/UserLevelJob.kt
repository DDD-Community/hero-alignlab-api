package com.hero.alignlab.batch.user.job

import com.hero.alignlab.domain.log.infrastructure.SystemActionLogRepository
import com.hero.alignlab.domain.user.infrastructure.UserInfoRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class UserLevelJob(
    private val systemActionLogRepository: SystemActionLogRepository,
    private val userInfoRepository: UserInfoRepository,
) {
    fun run() {
        val toDate = LocalDateTime.now()
        val fromDate = LocalDateTime.now().minusDays(1)

        val activeUsers = systemActionLogRepository.countActiveUser(fromDate, toDate, 1000)
            .filter { it.count > 100 }
            .map { it.uid }

        val users = userInfoRepository.findAllById(activeUsers)
            .mapNotNull {
                if (it.maxLevel) {
                    return@mapNotNull null
                }

                it.apply { this.level += 1 }
            }

        userInfoRepository.saveAll(users)
    }
}
