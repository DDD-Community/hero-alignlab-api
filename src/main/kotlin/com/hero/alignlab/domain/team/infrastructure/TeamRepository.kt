package com.hero.alignlab.domain.team.infrastructure

import com.hero.alignlab.domain.team.domain.Team
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(readOnly = true)
interface TeamRepository : JpaRepository<Team, Long> {
    fun existsByName(name: String): Boolean
}
