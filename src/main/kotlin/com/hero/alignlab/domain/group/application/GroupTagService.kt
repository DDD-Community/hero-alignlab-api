package com.hero.alignlab.domain.group.application

import com.hero.alignlab.domain.group.domain.GroupTag
import com.hero.alignlab.domain.group.domain.GroupTagMap
import com.hero.alignlab.domain.group.infrastructure.GroupTagMapRepository
import com.hero.alignlab.domain.group.infrastructure.GroupTagRepository
import com.hero.alignlab.domain.group.model.request.CreateGroupTagRequest
import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.InvalidRequestException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GroupTagService(
    private val groupTagRepository: GroupTagRepository,
    private val groupTagMapRepository: GroupTagMapRepository,
) {
    @Transactional
    fun saveSync(request: CreateGroupTagRequest): List<GroupTag> {
        return request.tagNames.map { tagName ->
            findOrCreateTag(tagName).also { tag ->
                groupTagMapRepository.save(GroupTagMap(groupId = request.groupId, tagId = tag.id))
            }
        }
    }

    @Transactional
    fun deleteSyncGroupId(groupId: Long) {
        groupTagMapRepository.deleteByGroupId(groupId)
    }

    suspend fun findByGroupId(groupId: Long): List<GroupTag> {
        return withContext(Dispatchers.IO) {
            val tagIds = groupTagMapRepository.findByGroupId(groupId)
                .map { it.tagId }
            groupTagRepository.findByIdIn(tagIds)
        }
    }

    private fun findOrCreateTag(tagName: String): GroupTag {
        return groupTagRepository.findByName(tagName)
            .firstOrNull() ?: groupTagRepository.save(GroupTag(name = tagName))
    }

    fun validateGroupTag(tagNames: List<String>) {
        if (tagNames.size > 3) {
            throw InvalidRequestException(ErrorCode.OVER_COUNT_GROUP_TAG_ERROR)
        }

        if (tagNames.size != tagNames.distinct().size) {
            throw InvalidRequestException(ErrorCode.DUPLICATE_GROUP_TAG_NAME_ERROR)
        }
    }
}