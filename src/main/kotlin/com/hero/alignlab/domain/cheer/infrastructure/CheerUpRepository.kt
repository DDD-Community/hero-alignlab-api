package com.hero.alignlab.domain.cheer.infrastructure

import com.hero.alignlab.domain.cheer.domain.CheerUp
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Transactional(readOnly = true)
@Repository
interface CheerUpRepository : JpaRepository<CheerUp, Long> {
    fun countAllByCheeredAtAndUid(cheeredAt: LocalDate, uid: Long): Long

    fun countAllByCheeredAtAndTargetUid(cheeredAt: LocalDate, targetUid: Long): Long

    fun existsByUidAndTargetUidAndCheeredAt(uid: Long, targetUid: Long, cheeredAt: LocalDate): Boolean

    fun findAllByTargetUidInAndCheeredAt(targetUids: Set<Long>, cheeredAt: LocalDate): List<CheerUp>

    fun findAllByUidAndCheeredAt(uid: Long, cheeredAt: LocalDate): List<CheerUp>

    fun deleteAllByUid(uid: Long)

    fun deleteAllByUidNotIn(uids: List<Long>)
}
