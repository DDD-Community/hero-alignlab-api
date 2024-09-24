package com.hero.alignlab.domain.group.infrastructure

import com.hero.alignlab.domain.group.domain.GroupUserScore
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Repository
interface GroupUserScoreRepository : JpaRepository<GroupUserScore, Long> {
    fun findAllByGroupId(groupId: Long): List<GroupUserScore>

    fun findByUid(uid: Long): GroupUserScore?

    fun findAllByGroupUserIdIn(groupUserIds: List<Long>): List<GroupUserScore>

    fun findAllByGroupIdAndUidIn(groupId: Long, uids: List<Long>): List<GroupUserScore>

    fun findAllByUidIn(uids: List<Long>): List<GroupUserScore>

    fun deleteAllByModifiedAtBefore(modifiedAt: LocalDateTime)

    fun deleteAllByUid(uid: Long)
}
