package com.hero.alignlab.domain.pose.domain

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "pose_count")
class PoseCount(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = -1,

    @Column(name = "uid")
    val uid: Long,

    /** 집계 데이터 */
    @Column(name = "total_count")
    @Convert(converter = PoseTotalCountConverter::class)
    val totalCount: PoseTotalCount,

    /** 기준 날짜 */
    @Column(name = "`date`")
    val date: LocalDate,
)
