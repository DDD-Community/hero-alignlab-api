package com.hero.alignlab.event.listener

import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.discussion.infrastructure.DiscussionCommentRepository
import com.hero.alignlab.domain.discussion.infrastructure.DiscussionRepository
import com.hero.alignlab.domain.group.infrastructure.GroupRepository
import com.hero.alignlab.domain.group.infrastructure.GroupUserRepository
import com.hero.alignlab.domain.notification.infrastructure.PoseNotificationRepository
import com.hero.alignlab.event.model.WithdrawEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class WithdrawEventListener(
    private val txTemplates: TransactionTemplates,
    private val discussionRepository: DiscussionRepository,
    private val groupRepository: GroupRepository,
    private val groupUserRepository: GroupUserRepository,
    private val poseNotificationRepository: PoseNotificationRepository,
    private val discussionCommentRepository: DiscussionCommentRepository,
) {
    @TransactionalEventListener
    fun handle(event: WithdrawEvent) {
        CoroutineScope(Dispatchers.IO).launch {
            txTemplates.writer.executeWithoutResult {
                // TODO: 회원 탈퇴 로직 필요.
            }
        }
    }
}
