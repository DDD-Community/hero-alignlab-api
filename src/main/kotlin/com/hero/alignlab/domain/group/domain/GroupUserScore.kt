package com.hero.alignlab.domain.group.domain

import com.hero.alignlab.domain.common.domain.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "`group_user_score`")
class GroupUserScore(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "group_id")
    val groupId: Long,

    @Column(name = "group_user_id")
    val groupUserId: Long,

    @Column(name = "uid")
    val uid: Long,

    @Column(name = "score")
    var score: Int?,
) : BaseEntity()
