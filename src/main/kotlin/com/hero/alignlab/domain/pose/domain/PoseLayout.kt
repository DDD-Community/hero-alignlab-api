package com.hero.alignlab.domain.pose.domain

import com.hero.alignlab.domain.common.domain.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "pose_layout")
class PoseLayout(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0L,

    @Column(name = "uid")
    val uid: Long,
) : BaseEntity()
