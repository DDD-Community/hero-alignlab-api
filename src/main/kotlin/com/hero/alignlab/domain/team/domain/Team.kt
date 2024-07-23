package com.hero.alignlab.domain.team.domain

import com.hero.alignlab.domain.common.domain.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "team")
data class Team(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @Column(name = "name")
    val name: String,

    @Column(name = "description")
    val description: String?,

    @Column(name = "owner_uid")
    val ownerUid: Long,
) : BaseEntity()
