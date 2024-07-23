package com.hero.alignlab.event.listener

import com.hero.alignlab.common.extension.executes
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.team.domain.TeamUser
import com.hero.alignlab.domain.team.infrastructure.TeamUserRepository
import com.hero.alignlab.event.model.CreateTeamEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class TeamEventListener(
    private val teamUserRepository: TeamUserRepository,
    private val txTemplates: TransactionTemplates,
) {
    private val logger = KotlinLogging.logger { }

    @TransactionalEventListener
    fun handle(event: CreateTeamEvent) {
        txTemplates.newTxWriter.executes {
            TeamUser(
                teamId = event.team.id,
                uid = event.team.ownerUid
            ).run { teamUserRepository.save(this) }
        }
    }
}
