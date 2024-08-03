package com.hero.alignlab.domain.pose.infrastructure

import com.hero.alignlab.common.extension.execute
import com.hero.alignlab.common.extension.isGoe
import com.hero.alignlab.common.extension.isLoe
import com.hero.alignlab.domain.pose.domain.PoseCount
import com.hero.alignlab.domain.pose.domain.QPoseCount
import com.hero.alignlab.domain.pose.infrastructure.model.PostCountSearchSpec
import com.querydsl.jpa.impl.JPAQuery
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Transactional(readOnly = true)
@Repository
interface PoseCountRepository : JpaRepository<PoseCount, Long>, PostCountQRepository {
    fun findByDate(date: LocalDate): PoseCount?
}

@Transactional(readOnly = true)
interface PostCountQRepository {
    fun search(spec: PostCountSearchSpec, pageable: Pageable): Page<PoseCount>
}

class PostCountQRepositoryImpl : PostCountQRepository, QuerydslRepositorySupport(PoseCount::class.java) {
    @Autowired
    @Qualifier("heroEntityManager")
    override fun setEntityManager(entityManager: EntityManager) {
        super.setEntityManager(entityManager)
    }

    private val qPoseCount = QPoseCount.poseCount

    override fun search(spec: PostCountSearchSpec, pageable: Pageable): Page<PoseCount> {
        val query = JPAQuery<QPoseCount>(entityManager)
            .select(qPoseCount)
            .from(qPoseCount)
            .where(
                qPoseCount.uid.eq(spec.uid),
                qPoseCount.date.isGoe(spec.fromDate),
                qPoseCount.date.isLoe(spec.toDate)
            )

        return querydsl.execute(query, pageable)
    }
}
