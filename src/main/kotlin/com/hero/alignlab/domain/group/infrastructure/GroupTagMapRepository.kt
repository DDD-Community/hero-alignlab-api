package com.hero.alignlab.domain.group.infrastructure

import com.hero.alignlab.domain.group.domain.GroupTagMap
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GroupTagMapRepository : JpaRepository<GroupTagMap, Long> {
    fun findByGroupId(groupId: Long): MutableList<GroupTagMap>
    fun deleteByGroupId(groupId: Long)
}