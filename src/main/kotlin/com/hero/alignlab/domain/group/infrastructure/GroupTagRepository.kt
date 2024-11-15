package com.hero.alignlab.domain.group.infrastructure

import com.hero.alignlab.domain.group.domain.GroupTag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GroupTagRepository : JpaRepository<GroupTag, Long> {
    fun findByName(name: String): List<GroupTag>

    fun findByIdIn(ids: List<Long>): List<GroupTag>
}