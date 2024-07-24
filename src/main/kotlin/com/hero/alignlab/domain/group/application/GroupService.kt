package com.hero.alignlab.domain.group.application

import com.hero.alignlab.common.extension.executes
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.group.domain.Group
import com.hero.alignlab.domain.group.infrastructure.GroupRepository
import com.hero.alignlab.domain.group.model.request.CreateGroupRequest
import com.hero.alignlab.domain.group.model.response.CreateGroupResponse
import com.hero.alignlab.event.model.CreateGroupEvent
import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.InvalidRequestException
import com.hero.alignlab.exception.NotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class GroupService(
    private val groupRepository: GroupRepository,
    private val txTemplates: TransactionTemplates,
    private val publisher: ApplicationEventPublisher,
) {
    suspend fun createGroup(user: AuthUser, request: CreateGroupRequest): CreateGroupResponse {
        if (existsByName(request.name)) {
            throw InvalidRequestException(ErrorCode.DUPLICATE_GROUP_NAME_ERROR)
        }

        val createdGroup = txTemplates.writer.executes {
            val group = groupRepository.save(
                Group(
                    name = request.name,
                    description = request.description,
                    ownerUid = user.uid
                )
            )

            publisher.publishEvent(CreateGroupEvent(group))

            group
        }

        return CreateGroupResponse.from(createdGroup)
    }

    suspend fun existsByName(name: String): Boolean {
        return withContext(Dispatchers.IO) {
            groupRepository.existsByName(name)
        }
    }

    suspend fun findByIdOrThrow(id: Long): Group {
        return findByIdOrNull(id) ?: throw NotFoundException(ErrorCode.NOT_FOUND_GROUP_ERROR)
    }

    suspend fun findByIdOrNull(id: Long): Group? {
        return withContext(Dispatchers.IO) {
            groupRepository.findByIdOrNull(id)
        }
    }
}
