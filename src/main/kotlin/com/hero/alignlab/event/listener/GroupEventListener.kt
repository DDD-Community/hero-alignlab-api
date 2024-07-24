package com.hero.alignlab.event.listener

import com.hero.alignlab.common.extension.executes
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.group.domain.GroupUser
import com.hero.alignlab.domain.group.infrastructure.GroupUserRepository
import com.hero.alignlab.event.model.CreateGroupEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class GroupEventListener(
    private val groupUserRepository: GroupUserRepository,
    private val txTemplates: TransactionTemplates,
) {
    private val logger = KotlinLogging.logger { }

    @TransactionalEventListener
    fun handle(event: CreateGroupEvent) {
        txTemplates.newTxWriter.executes {
            GroupUser(
                groupId = event.group.id,
                uid = event.group.ownerUid
            ).run { groupUserRepository.save(this) }
        }
    }
}
