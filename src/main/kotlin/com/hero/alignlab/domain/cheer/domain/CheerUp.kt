package com.hero.alignlab.domain.cheer.domain

import com.hero.alignlab.domain.common.domain.BaseEntity
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "cheer_up")
class CheerUp(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "uid")
    val uid: Long,

    @Column(name = "target_uid")
    val targetUid: Long,

    @Column(name = "cheered_at")
    val cheeredAt: LocalDate,
) : BaseEntity()
