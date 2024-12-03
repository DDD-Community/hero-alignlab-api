package com.hero.alignlab.domain.cheer.application

import com.hero.alignlab.common.extension.executes
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.cheer.domain.CheerUp
import com.hero.alignlab.domain.cheer.infrastructure.CheerUpRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CheerUpService(
    private val cheerUpRepository: CheerUpRepository,
    private val txTemplates: TransactionTemplates,
) {
    suspend fun countAllByCheeredAtAndUid(cheeredAt: LocalDate, uid: Long): Long {
        return withContext(Dispatchers.IO) {
            cheerUpRepository.countAllByCheeredAtAndUid(cheeredAt, uid)
        }
    }

    suspend fun existsByUidAndTargetUidAndCheeredAt(uid: Long, targetUid: Long, cheeredAt: LocalDate): Boolean {
        return withContext(Dispatchers.IO) {
            cheerUpRepository.existsByUidAndTargetUidAndCheeredAt(uid, targetUid, cheeredAt)
        }
    }

    suspend fun save(cheerUp: CheerUp): CheerUp {
        return txTemplates.writer.executes {
            cheerUpRepository.save(cheerUp)
        }
    }

    suspend fun findAllByTargetUidInAndCheeredAt(targetUids: Set<Long>, cheeredAt: LocalDate): List<CheerUp> {
        return withContext(Dispatchers.IO) {
            cheerUpRepository.findAllByTargetUidInAndCheeredAt(targetUids, cheeredAt)
        }
    }
}
