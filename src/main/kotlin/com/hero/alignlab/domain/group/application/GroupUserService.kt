package com.hero.alignlab.domain.group.application

import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.group.domain.GroupUser
import com.hero.alignlab.domain.group.infrastructure.GroupUserRepository
import com.hero.alignlab.domain.group.model.response.GroupUserResponse
import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.NotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GroupUserService(
    private val groupUserRepository: GroupUserRepository,
) {
    suspend fun existsByUid(uid: Long): Boolean {
        return withContext(Dispatchers.IO) {
            groupUserRepository.existsByUid(uid)
        }
    }

    suspend fun findAllByUid(uid: Long): List<GroupUser> {
        return withContext(Dispatchers.IO) {
            findAllByUidSync(uid)
        }
    }

    fun findAllByUidSync(uid: Long): List<GroupUser> {
        return groupUserRepository.findAllByUid(uid)
    }

    suspend fun findAllByGroupIdAndUids(groupId: Long, uids: List<Long>): List<GroupUser> {
        return withContext(Dispatchers.IO) {
            findAllByGroupIdAndUidsSync(groupId, uids)
        }
    }

    fun findAllByGroupIdAndUidsSync(groupId: Long, uids: List<Long>): List<GroupUser> {
        return groupUserRepository.findAllByGroupIdAndUidIn(groupId, uids)
    }

    suspend fun findTop1ByGroupIdAndUidNotOrderByCreatedAtAsc(groupId: Long, uid: Long): GroupUser? {
        return withContext(Dispatchers.IO) {
            groupUserRepository.findTop1ByGroupIdAndUidNotOrderByCreatedAtAsc(groupId, uid)
        }
    }

    @Transactional
    fun deleteByGroupIdAndUidSync(groupId: Long, uid: Long) {
        groupUserRepository.deleteByGroupIdAndUid(groupId, uid)
    }

    @Transactional
    fun delete(groupUser: GroupUser) {
        groupUserRepository.delete(groupUser)
    }

    @Transactional
    fun saveSync(groupId: Long, uid: Long): GroupUser {
        return groupUserRepository.save(GroupUser(groupId = groupId, uid = uid))
    }

    suspend fun existsByGroupIdAndUid(groupId: Long, uid: Long): Boolean {
        return withContext(Dispatchers.IO) {
            groupUserRepository.existsByGroupIdAndUid(groupId, uid)
        }
    }

    suspend fun findByGroupIdAndUidOrThrow(groupId: Long, uid: Long): GroupUser {
        return findByGroupIdAndUid(groupId, uid)
            ?: throw NotFoundException(ErrorCode.NOT_FOUND_GROUP_USER_ERROR)
    }

    suspend fun findByGroupIdAndUid(groupId: Long, uid: Long): GroupUser? {
        return withContext(Dispatchers.IO) {
            groupUserRepository.findByGroupIdAndUid(groupId, uid)
        }
    }

    suspend fun getGroupUsers(user: AuthUser, groupId: Long, pageable: Pageable): Page<GroupUserResponse> {
        val exists = existsByGroupIdAndUid(groupId, user.uid)

        if (!exists) {
            throw NotFoundException(ErrorCode.NOT_FOUND_GROUP_ERROR)
        }

        return findAllByGroupId(groupId, pageable)
            .map { groupUser -> GroupUserResponse(groupUser.id, groupUser.uid) }
    }

    suspend fun findAllByGroupId(groupId: Long, pageable: Pageable): Page<GroupUser> {
        return withContext(Dispatchers.IO) {
            groupUserRepository.findAllByGroupId(groupId, pageable)
        }
    }

    suspend fun findByIdOrNull(groupUserId: Long): GroupUser? {
        return withContext(Dispatchers.IO) {
            groupUserRepository.findByIdOrNull(groupUserId)
        }
    }

    @Transactional
    fun deleteSync(groupUserId: Long) {
        groupUserRepository.deleteById(groupUserId)
    }

    suspend fun findByUidOrThrow(uid: Long): GroupUser {
        return findByUidOrNull(uid) ?: throw NotFoundException(ErrorCode.NOT_FOUND_GROUP_USER_ERROR)
    }

    suspend fun findByUidOrNull(uid: Long): GroupUser? {
        return withContext(Dispatchers.IO) {
            groupUserRepository.findByUid(uid)
        }
    }

    suspend fun countAllByGroupId(groupId: Long): Long {
        return withContext(Dispatchers.IO) {
            groupUserRepository.countAllByGroupId(groupId)
        }
    }

    suspend fun findAll(): List<GroupUser> {
        return withContext(Dispatchers.IO) {
            groupUserRepository.findAll()
        }
    }

    suspend fun findByUidAndGroupIdIn(uid: Long, groupIds: List<Long>): List<GroupUser> {
        return withContext(Dispatchers.IO) {
            groupUserRepository.findByUidAndGroupIdIn(uid, groupIds)
        }
    }

    suspend fun findAllByGroupId(groupId: Long): List<GroupUser> {
        return withContext(Dispatchers.IO) {
            groupUserRepository.findAllByGroupId(groupId)
        }
    }
}
