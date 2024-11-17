package com.hero.alignlab.domain.group.infrastructure

import com.hero.alignlab.domain.group.domain.GroupTag

interface GroupTagQRepository {
    fun findByGroupId(groupId: Long): List<GroupTag>

    fun deleteGroupTagMapByGroupId(groupId: Long)
}