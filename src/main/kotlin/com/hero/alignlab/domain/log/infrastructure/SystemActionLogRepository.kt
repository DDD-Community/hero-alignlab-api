package com.hero.alignlab.domain.log.infrastructure

import com.hero.alignlab.domain.log.domain.SystemActionLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Repository
interface SystemActionLogRepository : JpaRepository<SystemActionLog, Long>
