package com.hero.alignlab.domain.pose.infrastructure

import com.hero.alignlab.domain.pose.domain.PoseSnapshot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Repository
interface PoseSnapshotRepository : JpaRepository<PoseSnapshot, Long>
