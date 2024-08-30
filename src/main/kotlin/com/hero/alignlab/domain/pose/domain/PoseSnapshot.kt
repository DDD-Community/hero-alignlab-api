package com.hero.alignlab.domain.pose.domain

import com.hero.alignlab.domain.common.domain.BaseEntity
import com.hero.alignlab.domain.pose.domain.vo.PoseType
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

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    val type: PoseType,

    @Column(name = "image_url")
    val imageUrl: String? = null,
) : BaseEntity()
