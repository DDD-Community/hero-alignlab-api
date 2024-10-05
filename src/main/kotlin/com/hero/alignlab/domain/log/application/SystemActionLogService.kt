package com.hero.alignlab.domain.log.application

import com.hero.alignlab.common.extension.coExecute
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.log.domain.SystemActionLog
import com.hero.alignlab.domain.log.infrastructure.SystemActionLogRepository
import com.hero.alignlab.domain.log.infrastructure.model.CountActiveUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class SystemActionLogService(
    private val systemActionLogRepository: SystemActionLogRepository,
    private val txTemplates: TransactionTemplates,
) {
    @Transactional
    suspend fun record(systemActionLog: SystemActionLog): SystemActionLog {
        return txTemplates.writer.coExecute {
            systemActionLogRepository.save(systemActionLog)
        }
    }

    suspend fun countActiveUser(
        fromCreatedAt: LocalDateTime,
        toCreatedAt: LocalDateTime,
        limit: Long = 3L
    ): List<CountActiveUser> {
        return withContext(Dispatchers.IO) {
            systemActionLogRepository.countActiveUser(fromCreatedAt, toCreatedAt, limit)
        }
    }
}
