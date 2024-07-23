package com.hero.alignlab.domain.team.domain

import com.hero.alignlab.domain.common.domain.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "team_user")
data class TeamUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @Column(name = "team_id")
    val teamId: Long,

    @Column(name = "uid")
    val uid: Long,
) : BaseEntity()
