package com.hero.alignlab.common.extension

import com.hero.alignlab.exception.HeroException
import com.hero.alignlab.exception.ErrorCode
import com.querydsl.core.types.dsl.*
import com.querydsl.jpa.impl.JPAQuery
import org.springframework.data.domain.*
import org.springframework.data.jpa.repository.support.Querydsl
import java.time.LocalDate
import java.time.LocalDateTime

fun <T> Querydsl?.execute(query: JPAQuery<T>, pageable: Pageable): Page<T> {
    return this.takeUnless { querydsl -> querydsl == null }
        ?.let { queryDsl ->
            queryDsl.applyPagination(pageable, query).run {
                PageImpl(this.fetch(), pageable, this.fetchCount())
            }
        } ?: throw HeroException(ErrorCode.QUERY_DSL_NOT_EXISTS_ERROR)
}

fun <T> Querydsl?.executeSlice(query: JPAQuery<T>, pageable: Pageable): Slice<T> {
    return this.takeUnless { querydsl -> querydsl == null }
        ?.let { queryDsl ->
            queryDsl.applyPagination(pageable, query).run {
                this.limit(pageable.pageSize + 1L)
                    .fetch()
            }.run {
                var hasNext = false
                if (this.size > pageable.pageSize) {
                    hasNext = true
                    this.removeAt(pageable.pageSize)
                }
                SliceImpl(this, pageable, hasNext)
            }
        } ?: throw HeroException(ErrorCode.QUERY_DSL_NOT_EXISTS_ERROR)
}

fun StringPath.isEquals(parameter: String?): BooleanExpression? {
    return parameter?.let { param -> this.eq(param) }
}

fun NumberPath<Long>.isEquals(parameter: Long?): BooleanExpression? {
    return parameter?.let { param -> this.eq(param) }
}

fun StringPath.isContains(parameter: String?): BooleanExpression? {
    return parameter?.let { param -> this.contains(param) }
}

fun NumberPath<Long>.isIn(parameters: Set<Long>?): BooleanExpression? {
    return parameters.takeUnless { params -> params.isNullOrEmpty() }?.let { params -> this.`in`(params) }
}

fun <T : Enum<T>> EnumPath<T>.isIn(parameters: Set<T>?): BooleanExpression? {
    return parameters?.takeIf { params -> params.isNotEmpty() }?.let { params -> this.`in`(params) }
}

fun NumberPath<Long>.isGoe(parameter: Long?): BooleanExpression? {
    return parameter?.let { param -> this.goe(param) }
}

fun NumberExpression<Long>.isGoe(parameter: Long?): BooleanExpression? {
    return parameter?.let { param -> this.goe(param) }
}

fun NumberExpression<Long>.isLoe(parameter: Long?): BooleanExpression? {
    return parameter?.let { param -> this.loe(param) }
}

fun NumberPath<Long>.isLoe(parameter: Long?): BooleanExpression? {
    return parameter?.let { param -> this.loe(param) }
}

fun NumberPath<Long>.isNotIn(parameters: Set<Long>?): BooleanExpression? {
    return parameters.takeUnless { params -> params.isNullOrEmpty() }?.let { params -> this.notIn(params) }
}

fun DateTimePath<LocalDateTime>.isGoe(parameter: LocalDateTime?): BooleanExpression? {
    return parameter?.let { param -> this.goe(param) }
}

fun DateTimePath<LocalDateTime>.isLoe(parameter: LocalDateTime?): BooleanExpression? {
    return parameter?.let { param -> this.loe(param) }
}

fun DatePath<LocalDate>.isGoe(parameter: LocalDate?): BooleanExpression? {
    return parameter?.let { param -> this.goe(param) }
}

fun DatePath<LocalDate>.isLoe(parameter: LocalDate?): BooleanExpression? {
    return parameter?.let { param -> this.loe(param) }
}
