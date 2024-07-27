package com.hero.alignlab.domain.group.infrastructure

import com.hero.alignlab.domain.group.domain.GroupUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Repository
interface GroupUserRepository : JpaRepository<GroupUser, Long> {
    fun findAllByUid(uid: Long): List<GroupUser>

    fun findAllByGroupIdAndUidIn(groupId: Long, uids: Set<Long>): List<GroupUser>

    fun findTop1ByGroupIdOrderByCreatedAtAsc(groupId: Long): GroupUser?

    fun deleteByGroupIdAndUid(groupId: Long, uid: Long)

    fun existsByGroupIdAndUid(groupId: Long, uid: Long): Boolean

    fun findAllByGroupId(groupId: Long, pageable: Pageable): Page<GroupUser>
}
