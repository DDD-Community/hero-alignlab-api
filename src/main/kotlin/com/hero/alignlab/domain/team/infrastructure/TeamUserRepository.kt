package com.hero.alignlab.domain.team.infrastructure

import com.hero.alignlab.domain.team.domain.TeamUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TeamUserRepository : JpaRepository<TeamUser, Long>
