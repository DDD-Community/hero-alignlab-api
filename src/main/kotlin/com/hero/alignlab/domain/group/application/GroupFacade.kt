package com.hero.alignlab.domain.group.application

import arrow.fx.coroutines.parZip
import com.hero.alignlab.common.extension.executes
import com.hero.alignlab.common.extension.executesOrNull
import com.hero.alignlab.common.model.HeroPageRequest
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.group.domain.Group
import com.hero.alignlab.domain.group.model.CreateGroupContext
import com.hero.alignlab.domain.group.model.request.CreateGroupRequest
import com.hero.alignlab.domain.group.model.response.CreateGroupResponse
import com.hero.alignlab.domain.group.model.response.GetGroupResponse
import com.hero.alignlab.domain.group.model.response.JoinGroupResponse
import com.hero.alignlab.domain.group.model.response.SearchGroupResponse
import com.hero.alignlab.event.model.CreateGroupEvent
import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.InvalidRequestException
import com.hero.alignlab.exception.NotFoundException
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
class GroupFacade(
    private val groupService: GroupService,
    private val groupUserService: GroupUserService,
    private val txTemplates: TransactionTemplates,
    private val publisher: ApplicationEventPublisher,
) {
    suspend fun createGroup(user: AuthUser, request: CreateGroupRequest): CreateGroupResponse {
        return parZip(
            { groupService.existsByName(request.name) },
            { groupUserService.existsByUid(user.uid) }
        ) { existsByName, existsByUid ->
            if (existsByName) {
                throw InvalidRequestException(ErrorCode.DUPLICATE_GROUP_NAME_ERROR)
            }

            if (existsByUid) {
                throw InvalidRequestException(ErrorCode.DUPLICATE_GROUP_JOIN_ERROR)
            }

            val group = CreateGroupContext(user, request).create()

            val createdGroup = createGroup(user, group)

            CreateGroupResponse.from(createdGroup)
        }
    }

    fun createGroup(user: AuthUser, group: Group): Group {
        return txTemplates.writer.executes {
            val createdGroup = groupService.saveSync(group)

            publisher.publishEvent(CreateGroupEvent(createdGroup))

            createdGroup
        }
    }

    suspend fun withdraw(user: AuthUser, groupId: Long) {
        withdraw(groupId, user.uid)
    }

    suspend fun withdraw(groupId: Long, uid: Long) {
        val group = groupService.findByIdOrThrow(groupId)

        /** 그룹 승계 또는 제거 */
        if (group.ownerUid == uid) {
            withdrawGroupOwner(group)
        }

        /** 그룹원 제거 */
        withdrawGroupUser(group, uid)
    }

    private suspend fun withdrawGroupOwner(group: Group) {
        val groupUser = groupUserService.findTop1ByGroupIdOrderByCreatedAtAsc(group.id)

        txTemplates.writer.executesOrNull {
            when (groupUser == null) {
                /** 그룹 제거 */
                true -> groupService.deleteByIdSync(group.id)

                /** 그룹 승계 */
                false -> {
                    val succeedGroup = group.apply {
                        this.ownerUid = groupUser.uid
                    }
                    groupService.saveSync(succeedGroup)
                }
            }
        }
    }

    private suspend fun withdrawGroupUser(group: Group, uid: Long) {
        txTemplates.writer.executesOrNull {
            groupUserService.deleteBySync(group.id, uid)
            groupService.saveSync(group.apply { this.userCount -= 1 })
        }
    }

    suspend fun joinGroup(
        user: AuthUser,
        groupId: Long,
        joinCode: String?
    ): JoinGroupResponse {
        return parZip(
            { groupService.findByIdOrThrow(groupId) },
            { groupUserService.findAllByUid(user.uid).associateBy { it.groupId } }
        ) { group, groupUsers ->
            if (group.isHidden && group.joinCode != joinCode) {
                throw InvalidRequestException(ErrorCode.IMPOSSIBLE_TO_JOIN_GROUP_ERROR)
            }

            val groupUser = groupUsers[groupId]

            when {
                /** 이미 다른 그룹에 속해있는 유저 */
                groupUser == null && groupUsers.isNotEmpty() -> {
                    throw InvalidRequestException(ErrorCode.DUPLICATE_GROUP_JOIN_ERROR)
                }

                /** 이미 그룹원인 경우 */
                groupUser != null -> {
                    JoinGroupResponse(
                        groupId = group.id,
                        uid = groupUser.uid,
                        groupUserId = groupUser.id
                    )
                }

                /** 그룹에 조인 */
                else -> {
                    val createdGroupUser = txTemplates.writer.executes {
                        groupService.saveSync(group.apply { this.userCount += 1 })
                        groupUserService.saveSync(groupId, user.uid)
                    }

                    JoinGroupResponse(
                        groupId = createdGroupUser.groupId,
                        uid = createdGroupUser.uid,
                        groupUserId = createdGroupUser.id
                    )
                }
            }
        }
    }

    suspend fun getGroup(user: AuthUser, groupId: Long): GetGroupResponse {
        return parZip(
            { groupService.findByIdOrThrow(groupId) },
            { groupUserService.existsByGroupIdAndUid(groupId, user.uid) },
        ) { group, joinedGroup ->
            if (!joinedGroup) {
                throw NotFoundException(ErrorCode.NOT_FOUND_GROUP_ERROR)
            }

            GetGroupResponse.from(group).run {
                when (group.ownerUid == user.uid) {
                    true -> this
                    false -> this.copy(joinCode = null)
                }
            }
        }
    }

    suspend fun searchGroup(user: AuthUser, pageRequest: HeroPageRequest): Page<SearchGroupResponse> {
        return groupService.findAll(pageRequest.toDefault())
            .map { group -> SearchGroupResponse.from(group) }
    }

    suspend fun deleteGroupUser(user: AuthUser, groupUserId: Long) {
        val groupUser = groupUserService.findByIdOrNull(groupUserId)
            ?: throw NotFoundException(ErrorCode.NOT_FOUND_USER_ERROR)

        groupService.findByIdAndOwnerUid(groupUser.groupId, user.uid)
            ?: throw NotFoundException(ErrorCode.NOT_FOUND_USER_ERROR)

        groupUserService.deleteSync(groupUserId)
    }
}
