package com.hero.alignlab.domain.pose.infrastructure

import com.hero.alignlab.domain.pose.domain.PoseSnapshot
import com.hero.alignlab.domain.pose.domain.QPoseSnapshot
import com.hero.alignlab.domain.pose.infrastructure.model.PoseTypeCountModel
import com.hero.alignlab.domain.pose.infrastructure.model.QPoseTypeCountModel
import com.querydsl.jpa.impl.JPAQuery
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Transactional(readOnly = true)
@Repository
interface PoseSnapshotRepository : JpaRepository<PoseSnapshot, Long>, PoseSnapshotQRepository

@Transactional(readOnly = true)
interface PoseSnapshotQRepository {
    fun countByUidsAndDate(uids: List<Long>, date: LocalDate): List<PoseTypeCountModel>
}

class PoseSnapshotRepositoryImpl : PoseSnapshotQRepository, QuerydslRepositorySupport(PoseSnapshot::class.java) {
    @Autowired
    @Qualifier("heroEntityManager")
    override fun setEntityManager(entityManager: EntityManager) {
        super.setEntityManager(entityManager)
    }

    private val qPoseSnapshot = QPoseSnapshot.poseSnapshot

    override fun countByUidsAndDate(uids: List<Long>, date: LocalDate): List<PoseTypeCountModel> {
        return JPAQuery<QPoseSnapshot>(entityManager)
            .select(
                QPoseTypeCountModel(
                    qPoseSnapshot.uid,
                    qPoseSnapshot.type,
                    qPoseSnapshot.id.count()
                )
            )
            .from(qPoseSnapshot)
            .where(
                qPoseSnapshot.uid.`in`(uids)
                    .and(qPoseSnapshot.createdAt.year().eq(date.year))
                    .and(qPoseSnapshot.createdAt.month().eq(date.monthValue))
                    .and(qPoseSnapshot.createdAt.dayOfMonth().eq(date.dayOfMonth))
            )
            .groupBy(qPoseSnapshot.type)
            .fetch()
    }
}
