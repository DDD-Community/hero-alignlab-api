package com.hero.alignlab.domain.group.application

import com.hero.alignlab.common.extension.executes
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.group.domain.Group
import com.hero.alignlab.domain.group.infrastructure.GroupRepository
import com.hero.alignlab.domain.group.model.UpdateGroupContext
import com.hero.alignlab.domain.group.model.request.UpdateGroupRequest
import com.hero.alignlab.domain.group.model.response.UpdateGroupResponse
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
class GroupService(
    private val groupRepository: GroupRepository,
    private val txTemplates: TransactionTemplates,
) {
    suspend fun updateGroup(user: AuthUser, id: Long, request: UpdateGroupRequest): UpdateGroupResponse {
        val group = findByIdAndOwnerUidOrThrow(id, user.uid)

        val updatedGroup = txTemplates.writer.executes {
            groupRepository.save(UpdateGroupContext(group, request).update())
        }

        return UpdateGroupResponse.from(updatedGroup)
    }

    suspend fun existsByName(name: String): Boolean {
        return withContext(Dispatchers.IO) {
            groupRepository.existsByName(name)
        }
    }

    suspend fun findByIdAndOwnerUidOrThrow(id: Long, ownerUid: Long): Group {
        return findByIdAndOwnerUid(id, ownerUid) ?: throw NotFoundException(ErrorCode.NOT_FOUND_GROUP_ERROR)
    }

    suspend fun findByIdAndOwnerUid(id: Long, ownerUid: Long): Group? {
        return withContext(Dispatchers.IO) {
            groupRepository.findByIdAndOwnerUid(id, ownerUid)
        }
    }

    suspend fun findByIdOrThrow(id: Long): Group {
        return findByIdOrNull(id) ?: throw NotFoundException(ErrorCode.NOT_FOUND_GROUP_ERROR)
    }

    suspend fun existsById(id: Long): Boolean {
        return withContext(Dispatchers.IO) { groupRepository.existsById(id) }
    }

    suspend fun findByIdOrNull(id: Long): Group? {
        return withContext(Dispatchers.IO) {
            groupRepository.findByIdOrNull(id)
        }
    }

    fun deleteByIdSync(id: Long) {
        groupRepository.deleteById(id)
    }

    @Transactional
    fun saveSync(group: Group): Group {
        return groupRepository.save(group)
    }

    suspend fun findAll(pageable: Pageable): Page<Group> {
        return withContext(Dispatchers.IO) {
            groupRepository.findAll(pageable)
        }
    }

    suspend fun findByOwnerUid(uid: Long): Group? {
        return withContext(Dispatchers.IO) {
            groupRepository.findByOwnerUid(uid)
        }
    }
}
