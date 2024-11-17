package com.hero.alignlab.domain.group.application

import com.hero.alignlab.domain.group.domain.Group
import com.hero.alignlab.domain.group.infrastructure.GroupRepository
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
) {
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

    suspend fun findByTagNameAndPage(tagName: String?, pageable: Pageable): Page<Group> {
        return withContext(Dispatchers.IO) {
            groupRepository.findByTagNameAndPage(tagName, pageable)
        }
    }

    suspend fun findByOwnerUid(uid: Long): Group? {
        return withContext(Dispatchers.IO) {
            groupRepository.findByOwnerUid(uid)
        }
    }
}
