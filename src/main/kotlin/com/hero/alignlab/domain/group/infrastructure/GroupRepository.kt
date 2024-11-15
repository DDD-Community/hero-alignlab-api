package com.hero.alignlab.domain.group.infrastructure

import com.hero.alignlab.domain.group.domain.Group
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
@Transactional(readOnly = true)
interface GroupRepository : JpaRepository<Group, Long>, GroupQRepository {
    fun existsByName(name: String): Boolean

    fun findByIdAndOwnerUid(id: Long, ownerUid: Long): Group?

    fun countByCreatedAtBetween(startAt: LocalDateTime, endAt: LocalDateTime): Long

    fun findByOwnerUid(ownerUid: Long): Group?

    fun deleteAllByOwnerUid(ownerUid: Long)
}
