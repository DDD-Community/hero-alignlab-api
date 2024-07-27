package com.hero.alignlab.domain.group.application

import arrow.fx.coroutines.parZip
import com.hero.alignlab.common.extension.executes
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.group.model.response.JoinGroupResponse
import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.InvalidRequestException
import org.springframework.stereotype.Service

@Service
class GroupFacade(
    private val groupService: GroupService,
    private val groupUserService: GroupUserService,
    private val txTemplates: TransactionTemplates,
) {
    suspend fun withdraw(user: AuthUser, groupId: Long) {
        val group = groupService.findByIdOrThrow(groupId)

        if (group.ownerUid == user.uid) {
            val groupUser = groupUserService.findTop1ByGroupIdOrderByCreatedAtAsc(groupId)

            if (groupUser == null) {
                groupService.deleteByIdSync(groupId)
            } else {
                group.apply {
                    this.ownerUid = groupUser.uid
                }.run { groupService.saveSync(this) }
            }
        } else {
            groupUserService.deleteBySync(groupId, user.uid)
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

            if (groupUser == null && groupUsers.isNotEmpty()) {
                throw InvalidRequestException(ErrorCode.DUPLICATE_GROUP_JOIN_ERROR)
            }

            if (groupUser != null) {
                JoinGroupResponse(
                    groupId = group.id,
                    uid = groupUser.uid,
                    groupUserId = groupUser.id
                )
            }

            val createdGroupUser = txTemplates.writer.executes {
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
