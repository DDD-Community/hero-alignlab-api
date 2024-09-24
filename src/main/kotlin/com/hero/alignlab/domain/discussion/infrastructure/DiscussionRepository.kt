package com.hero.alignlab.domain.discussion.infrastructure

import com.hero.alignlab.domain.discussion.domain.Discussion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Repository
interface DiscussionRepository : JpaRepository<Discussion, Long> {
    fun countByCreatedAtBetween(startAt: LocalDateTime, endAt: LocalDateTime): Long

    fun deleteAllByUid(uid: Long)
}
