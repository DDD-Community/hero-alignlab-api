package com.hero.alignlab.domain.group.infrastructure

import com.hero.alignlab.domain.group.domain.GroupTag
import com.hero.alignlab.domain.group.domain.QGroupTag.groupTag
import com.hero.alignlab.domain.group.domain.QGroupTagMap.groupTagMap
import com.querydsl.jpa.impl.JPAQueryFactory

class GroupTagQRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : GroupTagQRepository {
    override fun findByGroupId(groupId: Long): List<GroupTag> {
        return queryFactory.selectFrom(groupTag)
            .join(groupTagMap).on(groupTag.id.eq(groupTagMap.tagId))
            .where(groupTagMap.groupId.eq(groupId))
            .fetch()
    }

    override fun deleteGroupTagMapByGroupId(groupId: Long) {
        queryFactory.delete(groupTagMap)
            .where(groupTagMap.groupId.eq(groupId))
            .execute()
    }
}