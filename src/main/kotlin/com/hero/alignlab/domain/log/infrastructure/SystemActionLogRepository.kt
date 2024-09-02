package com.hero.alignlab.domain.log.infrastructure

import com.hero.alignlab.common.extension.isGoe
import com.hero.alignlab.common.extension.isLoe
import com.hero.alignlab.domain.log.domain.QSystemActionLog
import com.hero.alignlab.domain.log.domain.SystemActionLog
import com.hero.alignlab.domain.log.infrastructure.model.CountActiveUser
import com.hero.alignlab.domain.log.infrastructure.model.QCountActiveUser
import com.querydsl.jpa.impl.JPAQuery
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Repository
interface SystemActionLogRepository : JpaRepository<SystemActionLog, Long>, SystemActionLogQRepository {
    fun countByCreatedAtBetween(startAt: LocalDateTime, endAt: LocalDateTime): Long
}

@Transactional(readOnly = true)
interface SystemActionLogQRepository {
    fun countActiveUserTop3(fromCreatedAt: LocalDateTime, toCreatedAt: LocalDateTime): List<CountActiveUser>
}

class SystemActionLogRepositoryImpl : SystemActionLogQRepository,
    QuerydslRepositorySupport(SystemActionLog::class.java) {
    @Autowired
    @Qualifier("heroEntityManager")
    override fun setEntityManager(entityManager: EntityManager) {
        super.setEntityManager(entityManager)
    }

    private val qSystemActionLog = QSystemActionLog.systemActionLog

    override fun countActiveUserTop3(fromCreatedAt: LocalDateTime, toCreatedAt: LocalDateTime): List<CountActiveUser> {
        return JPAQuery<QSystemActionLog>(entityManager)
            .select(
                QCountActiveUser(
                    qSystemActionLog.uid,
                    qSystemActionLog.id.count()
                )
            ).from(qSystemActionLog)
            .where(
                qSystemActionLog.uid.isNotNull,
                qSystemActionLog.createdAt.isGoe(fromCreatedAt),
                qSystemActionLog.createdAt.isLoe(toCreatedAt)
            )
            .groupBy(qSystemActionLog.uid)
            .orderBy(qSystemActionLog.id.count().desc())
            .limit(3)
            .fetch()
    }
}
