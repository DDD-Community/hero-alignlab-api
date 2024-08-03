package com.hero.alignlab.domain.pose.infrastructure

import com.hero.alignlab.domain.pose.domain.PoseCount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Transactional(readOnly = true)
@Repository
interface PoseCountRepository : JpaRepository<PoseCount, Long> {
    fun findByDate(date: LocalDate): PoseCount?
}
