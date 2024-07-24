package com.hero.alignlab.domain.group.infrastructure

import com.hero.alignlab.domain.group.domain.GroupUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GroupUserRepository : JpaRepository<GroupUser, Long>
