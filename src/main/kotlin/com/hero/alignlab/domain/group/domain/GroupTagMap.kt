package com.hero.alignlab.domain.group.domain

import com.hero.alignlab.domain.common.domain.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "`group_tag_map`")
data class GroupTagMap (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @Column(name = "group_id")
    val groupId: Long,

    @Column(name = "tag_id")
    val tagId: Long
) : BaseEntity()