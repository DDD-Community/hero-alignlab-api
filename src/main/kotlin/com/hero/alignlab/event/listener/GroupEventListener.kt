package com.hero.alignlab.event.listener

import com.hero.alignlab.domain.group.application.GroupUserService
import com.hero.alignlab.event.model.CreateGroupEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class GroupEventListener(
    private val groupUserService: GroupUserService,
) {
    private val logger = KotlinLogging.logger { }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    @TransactionalEventListener
    fun handle(event: CreateGroupEvent) {
        groupUserService.saveSync(event.group.id, event.group.ownerUid)
    }
}
