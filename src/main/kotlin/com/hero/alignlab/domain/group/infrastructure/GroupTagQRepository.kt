package com.hero.alignlab.domain.group.infrastructure

import com.hero.alignlab.domain.group.domain.GroupTag
import com.hero.alignlab.domain.group.infrastructure.result.GroupTagQueryResult

interface GroupTagQRepository {
    fun findByGroupId(groupId: Long): List<GroupTag>

    fun findByGroupIds(groupIds: List<Long>): List<GroupTagQueryResult>

    fun deleteGroupTagMapByGroupId(groupId: Long)
}