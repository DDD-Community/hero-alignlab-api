package com.hero.alignlab.domain.group.domain

import com.hero.alignlab.domain.common.domain.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "`group_user`")
data class GroupUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "group_id")
    val groupId: Long,

    @Column(name = "uid")
    val uid: Long,
) : BaseEntity()
