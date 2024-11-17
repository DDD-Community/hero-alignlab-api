package com.hero.alignlab.domain.user.domain

import com.hero.alignlab.domain.common.domain.BaseEntity
import com.hero.alignlab.domain.user.domain.vo.OAuthProvider
import jakarta.persistence.*

@Entity
@Table(name = "oauth_user_info")
class OAuthUserInfo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "uid")
    val uid: Long,

    @Column(name = "provider")
    @Enumerated(EnumType.STRING)
    val provider: OAuthProvider,

    @Column(name = "oauth_id")
    val oauthId: String,
) : BaseEntity()
