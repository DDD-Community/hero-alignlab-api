package com.hero.alignlab.domain.discussion.domain

import com.hero.alignlab.domain.common.domain.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "discussion_comment")
class DiscussionComment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1L,

    @Column(name = "uid")
    val uid: Long,

    @Column(name = "discussion_id")
    val discussionId: Long,

    @Column(name = "title")
    val title: String,

    @Column(name = "content")
    val content: String,
) : BaseEntity()
