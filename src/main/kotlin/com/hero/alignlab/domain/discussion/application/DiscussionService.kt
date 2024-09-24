package com.hero.alignlab.domain.discussion.application

import com.hero.alignlab.common.extension.coExecute
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.discussion.domain.Discussion
import com.hero.alignlab.domain.discussion.infrastructure.DiscussionRepository
import com.hero.alignlab.domain.discussion.model.request.DiscussionRequest
import com.hero.alignlab.domain.discussion.model.response.DiscussionResponse
import com.hero.alignlab.event.model.DiscussionEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class DiscussionService(
    private val discussionRepository: DiscussionRepository,
    private val txTemplates: TransactionTemplates,
    private val publisher: ApplicationEventPublisher,
) {
    suspend fun create(user: AuthUser, request: DiscussionRequest): DiscussionResponse {
        val discussion = Discussion(
            uid = user.uid,
            email = request.email,
            type = request.type,
            title = request.title,
            content = request.content,
        )

        val createdDiscussion = txTemplates.writer.coExecute {
            val discussion = discussionRepository.save(discussion)

            publisher.publishEvent(DiscussionEvent(discussion))

            discussion
        }

        return DiscussionResponse.from(createdDiscussion)
    }
}
