package com.hero.alignlab.domain.discussion.model.response

import com.hero.alignlab.domain.discussion.domain.Discussion
import com.hero.alignlab.domain.discussion.domain.DiscussionType
import java.time.LocalDateTime

data class DiscussionResponse(
    val id: Long,
    val uid: Long,
    val type: DiscussionType,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
) {
    companion object {
        fun from(discussion: Discussion): DiscussionResponse {
            return DiscussionResponse(
                id = discussion.id,
                uid = discussion.uid,
                type = discussion.type,
                title = discussion.title,
                content = discussion.content,
                createdAt = discussion.createdAt,
                modifiedAt = discussion.modifiedAt
            )
        }
    }
}
