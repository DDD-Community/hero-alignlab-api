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
        val existsTags = groupTagRepository.findAllByNameIn(request.tagNames)
        val existsTagNames = existsTags.map { tag -> tag.name }

        val needToCreateTags = request.tagNames
            .filterNot { tagName -> existsTagNames.contains(tagName) }
            .map { tagName -> GroupTag(name = tagName) }

        val createdTags = groupTagRepository.saveAll(needToCreateTags)

        (createdTags + existsTags)
            .map { tag -> GroupTagMap(groupId = request.groupId, tagId = tag.id) }
            .run { groupTagMapRepository.saveAll(this) }

        return (createdTags + existsTags)
    }

    @Transactional
    fun deleteGroupTagMapSyncByGroupId(groupId: Long) {
        /**
         * 왜, QueryDSL로 삭제를 했는지
         * https://docs.jboss.org/hibernate/orm/4.2/javadocs/org/hibernate/event/internal/AbstractFlushingEventListener.html
         */
        groupTagRepository.deleteGroupTagMapByGroupId(groupId)
    }

    suspend fun findByGroupId(groupId: Long): List<GroupTag> {
        return withContext(Dispatchers.IO) {
            groupTagRepository.findByGroupId(groupId)
        }
    }

    suspend fun findGroupTagNamesByGroupIds(groupIds: List<Long>): Map<Long, List<String>> {
        return withContext(Dispatchers.IO) {
            groupTagRepository.findByGroupIds(groupIds)
                .groupBy { it.groupId }
                .mapValues { entry -> entry.value.map { it.tagName } }
        }
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