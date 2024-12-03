package com.hero.alignlab.domain.group.infrastructure

import com.hero.alignlab.domain.group.domain.GroupUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Repository
interface GroupUserRepository : JpaRepository<GroupUser, Long> {
    fun findAllByUid(uid: Long): List<GroupUser>

    fun findAllByGroupIdAndUidIn(groupId: Long, uids: List<Long>): List<GroupUser>

    fun findTop1ByGroupIdAndUidNotOrderByCreatedAtAsc(groupId: Long, uid: Long): GroupUser?

    fun deleteByGroupIdAndUid(groupId: Long, uid: Long)

    fun existsByGroupIdAndUid(groupId: Long, uid: Long): Boolean

    fun findByGroupIdAndUid(groupId: Long, uid: Long): GroupUser?

    fun findAllByGroupId(groupId: Long, pageable: Pageable): Page<GroupUser>

    fun existsByUid(uid: Long): Boolean

    fun countByCreatedAtBetween(startAt: LocalDateTime, endAt: LocalDateTime): Long

    fun findByUid(uid: Long): GroupUser?

    fun countAllByGroupId(groupId: Long): Long

    fun findByUidAndGroupIdIn(uid: Long, groupIds: List<Long>): List<GroupUser>

    fun deleteAllByUid(uid: Long)

    fun findAllByGroupId(groupId: Long): List<GroupUser>
}
