package com.hero.alignlab.domain.log.domain

import com.hero.alignlab.domain.common.domain.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "system_action_log")
class SystemActionLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "uid")
    val uid: Long? = null,

    @Column(name = "ip_address")
    val ipAddress: String? = null,

    val path: String? = null,

    @Column(name = "http_method")
    val httpMethod: String? = null,

    @Column(name = "user_agent")
    val userAgent: String? = null,

    val host: String? = null,

    val referer: String? = null,

    @Column(columnDefinition = "TEXT")
    val extra: String? = null,
) : BaseEntity()
