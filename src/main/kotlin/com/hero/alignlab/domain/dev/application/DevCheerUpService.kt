package com.hero.alignlab.domain.dev.application

import com.hero.alignlab.common.extension.executes
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.cheer.infrastructure.CheerUpRepository
import org.springframework.stereotype.Service

@Service
class DevCheerUpService(
    private val cheerUpRepository: CheerUpRepository,
    private val txTemplates: TransactionTemplates,
) {
    fun deleteAll() {
        txTemplates.writer.executes {
            cheerUpRepository.deleteAll()
        }
    }
}
