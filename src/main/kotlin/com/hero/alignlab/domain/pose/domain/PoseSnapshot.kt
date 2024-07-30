package com.hero.alignlab.domain.pose.domain

import com.hero.alignlab.domain.common.domain.BaseEntity
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "pose_snapshot")
data class PoseSnapshot(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = -1,

    @Column(name = "uid")
    val uid: Long,

    @Column(name = "score")
    val score: BigDecimal,
) : BaseEntity()
