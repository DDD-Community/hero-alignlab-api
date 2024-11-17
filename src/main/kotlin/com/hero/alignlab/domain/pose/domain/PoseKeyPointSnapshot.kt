package com.hero.alignlab.domain.pose.domain

import com.hero.alignlab.domain.pose.domain.vo.PosePosition
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "pose_key_point_snapshot")
class PoseKeyPointSnapshot(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0L,

    @Column(name = "pose_snapshot_id")
    val poseSnapshotId: Long,

    @Column(name = "position")
    @Enumerated(EnumType.STRING)
    val position: PosePosition,

    @Column(name = "x")
    val x: BigDecimal,

    @Column(name = "y")
    val y: BigDecimal,

    @Column(name = "confidence")
    val confidence: BigDecimal,
)
