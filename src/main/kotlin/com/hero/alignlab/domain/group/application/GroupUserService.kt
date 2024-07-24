package com.hero.alignlab.domain.group.application

import com.hero.alignlab.domain.group.domain.GroupUser
import com.hero.alignlab.domain.group.infrastructure.GroupUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class GroupUserService(
    private val groupUserRepository: GroupUserRepository,
) {
    suspend fun findAllByUid(uid: Long): List<GroupUser> {
        return withContext(Dispatchers.IO) {
            findAllByUid(uid)
        }
    }

    fun findAllByUidSync(uid: Long): List<GroupUser> {
        return groupUserRepository.findAllByUid(uid)
    }

    fun findAllByGroupIdAndUids(groupId: Long, uids: Set<Long>): List<GroupUser> {
        return groupUserRepository.findAllByGroupIdAndUidIn(groupId, uids)
    }
}
