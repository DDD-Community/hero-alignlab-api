package com.hero.alignlab.domain.group.infrastructure

import com.hero.alignlab.domain.group.domain.Group
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(readOnly = true)
interface GroupRepository : JpaRepository<Group, Long> {
    fun existsByName(name: String): Boolean
}
