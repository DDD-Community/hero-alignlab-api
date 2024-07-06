package com.hero.alignlab.domain.user.infrastructure

import com.hero.alignlab.domain.user.domain.UserInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Repository
interface UserInfoRepository : JpaRepository<UserInfo, Long>
