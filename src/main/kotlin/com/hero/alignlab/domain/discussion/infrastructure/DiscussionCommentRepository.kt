package com.hero.alignlab.domain.discussion.infrastructure

import com.hero.alignlab.domain.discussion.domain.DiscussionComment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Repository
interface DiscussionCommentRepository : JpaRepository<DiscussionComment, Long> {
    fun deleteAllByUid(uid: Long)
}
