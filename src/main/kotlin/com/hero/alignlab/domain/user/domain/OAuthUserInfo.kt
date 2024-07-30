package com.hero.alignlab.domain.user.domain

import com.hero.alignlab.domain.common.domain.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "oauth_user_info")
class OAuthUserInfo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @Column(name = "uid")
    val uid: Long,

    @Column(name = "provider")
    @Enumerated(EnumType.STRING)
    val provider: OAuthProvider,

    @Column(name = "oauth_id")
    val oauthId: String,
) : BaseEntity()
