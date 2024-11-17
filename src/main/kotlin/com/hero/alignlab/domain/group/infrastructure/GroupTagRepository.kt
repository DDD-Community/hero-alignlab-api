package com.hero.alignlab.domain.group.infrastructure

import com.hero.alignlab.domain.group.domain.GroupTag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Repository
interface GroupTagRepository : JpaRepository<GroupTag, Long>, GroupTagQRepository {
    fun findAllByNameIn(names: List<String>): Set<GroupTag>
}