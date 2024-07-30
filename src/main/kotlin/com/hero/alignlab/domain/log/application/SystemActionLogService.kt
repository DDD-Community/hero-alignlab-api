package com.hero.alignlab.domain.log.application

import com.hero.alignlab.domain.log.domain.SystemActionLog
import com.hero.alignlab.domain.log.infrastructure.SystemActionLogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SystemActionLogService(
    private val systemActionLogRepository: SystemActionLogRepository,
) {
    @Transactional
    fun record(systemActionLog: SystemActionLog) {
        systemActionLogRepository.save(systemActionLog)
    }
}
