package com.hero.alignlab.domain.group.infrastructure

import com.hero.alignlab.common.extension.isContains
import com.hero.alignlab.domain.group.domain.Group
import com.hero.alignlab.domain.group.domain.QGroup.group
import com.hero.alignlab.domain.group.domain.QGroupTag.groupTag
import com.hero.alignlab.domain.group.domain.QGroupTagMap.groupTagMap
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.ComparableExpressionBase
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class GroupQRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : GroupQRepository {
    override fun findByTagNameAndPage(tagName: String?, pageable: Pageable): Page<Group> {
        val groups = queryFactory
            .selectDistinct(group)
            .from(group)
            .leftJoin(groupTagMap).on(group.id.eq(groupTagMap.groupId))
            .leftJoin(groupTag).on(groupTagMap.tagId.eq(groupTag.id))
            .where(groupTag.name.isContains(tagName))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(getGroupOrderSpecifier(pageable))
            .fetch()

        val count = queryFactory
            .select(group.countDistinct())
            .from(group)
            .leftJoin(groupTagMap).on(group.id.eq(groupTagMap.groupId))
            .leftJoin(groupTag).on(groupTagMap.tagId.eq(groupTag.id))
            .where(groupTag.name.isContains(tagName))
            .orderBy(getGroupOrderSpecifier(pageable))
            .fetchOne() ?: 0L

        return PageImpl(groups, pageable, count)
    }

    private fun getGroupOrderSpecifier(pageable: Pageable): OrderSpecifier<out Comparable<*>?>? {
        val order = pageable.sort.firstOrNull() ?: return null
        val orderSpecifier: ComparableExpressionBase<out Comparable<*>> = when (order.property) {
            "createdAt" -> group.createdAt
            "name" -> group.name
            "ownerUid" -> group.ownerUid
            else -> group.createdAt
        }
        return if (order.isDescending) orderSpecifier.desc() else orderSpecifier.asc()
    }
}