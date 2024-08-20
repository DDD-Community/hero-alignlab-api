package com.hero.alignlab.domain.pose.domain

import com.hero.alignlab.domain.pose.domain.vo.PosePosition
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "pose_layout_point")
class PoseLayoutPoint(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = -1,

    @Column(name = "pose_layout_id")
    val poseLayoutId: Long,

    @Column(name = "position")
    @Enumerated(EnumType.STRING)
    val position: PosePosition,

    @Column(name = "x")
    val x: BigDecimal,

    @Column(name = "y")
    val y: BigDecimal,
)
