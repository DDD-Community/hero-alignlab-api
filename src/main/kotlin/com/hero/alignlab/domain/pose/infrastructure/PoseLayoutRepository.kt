package com.hero.alignlab.domain.pose.infrastructure

import com.hero.alignlab.domain.pose.domain.PoseLayout
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Repository
interface PoseLayoutRepository : JpaRepository<PoseLayout, Long> {
    fun findTop1ByUidOrderByIdDesc(uid: Long): PoseLayout?

    fun findByIdAndUid(id: Long, uid: Long): PoseLayout?

    fun findAllByUid(uid: Long): List<PoseLayout>

    fun deleteAllByUid(uid: Long)
}
