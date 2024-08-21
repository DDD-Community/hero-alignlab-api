package com.hero.alignlab.domain.group.infrastructure

import com.hero.alignlab.domain.group.domain.GroupUserScore
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Repository
interface GroupUserScoreRepository : JpaRepository<GroupUserScore, Long> {
    fun findAllByGroupId(groupId: Long): List<GroupUserScore>
}
