package com.hero.alignlab.domain.group.infrastructure

import com.hero.alignlab.domain.group.domain.GroupUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Repository
interface GroupUserRepository : JpaRepository<GroupUser, Long> {
    fun findAllByUid(uid: Long): List<GroupUser>

    fun findAllByGroupIdAndUidIn(groupId: Long, uids: Set<Long>): List<GroupUser>
}
