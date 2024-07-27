package com.hero.alignlab.domain.group.domain

import com.hero.alignlab.domain.common.domain.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "`group`")
data class Group(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @Column(name = "name")
    var name: String,

    @Column(name = "description")
    var description: String?,

    @Column(name = "owner_uid")
    var ownerUid: Long,

    @Column(name = "is_hidden")
    var isHidden: Boolean,

    @Column(name = "join_code")
    var joinCode: String
) : BaseEntity()
