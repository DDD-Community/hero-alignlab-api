package com.hero.alignlab.domain.user.domain

import com.hero.alignlab.common.encrypt.EncryptData
import com.hero.alignlab.domain.common.domain.BaseEntity
import com.hero.alignlab.domain.user.domain.converter.PasswordConverter
import jakarta.persistence.*

@Entity
@Table(name = "credential_user_info")
class CredentialUserInfo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "uid")
    val uid: Long,

    @Column(name = "username")
    val username: String,

    @Column(name = "password")
    @Convert(converter = PasswordConverter::class)
    val password: EncryptData,
) : BaseEntity()
