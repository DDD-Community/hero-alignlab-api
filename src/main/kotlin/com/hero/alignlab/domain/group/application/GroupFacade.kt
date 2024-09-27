package com.hero.alignlab.domain.group.application

import arrow.fx.coroutines.parZip
import com.hero.alignlab.common.extension.executes
import com.hero.alignlab.common.extension.executesOrNull
import com.hero.alignlab.common.model.HeroPageRequest
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.group.domain.Group
import com.hero.alignlab.domain.group.domain.GroupUser
import com.hero.alignlab.domain.group.domain.GroupUserScore
import com.hero.alignlab.domain.group.model.CreateGroupContext
import com.hero.alignlab.domain.group.model.request.CheckGroupRegisterRequest
import com.hero.alignlab.domain.group.model.request.CreateGroupRequest
import com.hero.alignlab.domain.group.model.response.*
import com.hero.alignlab.domain.pose.application.PoseSnapshotService
import com.hero.alignlab.domain.pose.domain.vo.PoseType.Companion.BAD_POSE
import com.hero.alignlab.domain.user.application.UserInfoService
import com.hero.alignlab.event.model.CreateGroupEvent
import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.InvalidRequestException
import com.hero.alignlab.exception.NotFoundException
import com.hero.alignlab.ws.handler.ReactiveGroupUserWebSocketHandler
import kotlinx.coroutines.*
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger

@Service
class GroupFacade(
    private val groupService: GroupService,
    private val groupUserService: GroupUserService,
    private val groupUserScoreService: GroupUserScoreService,
    private val userInfoService: UserInfoService,
    private val txTemplates: TransactionTemplates,
    private val publisher: ApplicationEventPublisher,
    private val wsHandler: ReactiveGroupUserWebSocketHandler,
    private val poseSnapshotService: PoseSnapshotService,
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
        withdraw(user.uid, groupId)
    }

    suspend fun withdraw(uid: Long, groupId: Long) {
        parZip(
            { groupService.findByIdOrThrow(groupId) },
            { groupUserService.findByGroupIdAndUidOrThrow(groupId, uid) }
        ) { group, groupUser ->
            when (group.ownerUid == uid) {
                /** 그룹 승계 또는 제거 */
                true -> withdrawGroupOwner(uid, group, groupUser)

                /** 그룹원 제거 */
                false -> withdrawGroupUser(group, uid, groupUser)
            }
        }

        txTemplates.writer.executes {
            groupUserScoreService.deleteAllByUid(uid)
        }
    }

    private suspend fun withdrawGroupOwner(uid: Long, group: Group, groupUser: GroupUser) {
        val otherGroupUser = groupUserService.findTop1ByGroupIdAndUidNotOrderByCreatedAtAsc(group.id, uid)

        txTemplates.writer.executesOrNull {
            when (otherGroupUser == null) {
                /** 그룹 제거 */
                true -> {
                    groupService.deleteByIdSync(group.id)
                }

                /** 그룹 승계 */
                false -> {
                    val succeedGroup = group.apply {
                        this.ownerUid = otherGroupUser.uid
                        this.userCount -= 1
                    }
                    groupService.saveSync(succeedGroup)
                }
            }

            groupUserService.delete(groupUser)
        }
    }

    private suspend fun withdrawGroupUser(group: Group, uid: Long, groupUser: GroupUser) {
        txTemplates.writer.executesOrNull {
            groupUserService.delete(groupUser)
            groupService.saveSync(group.apply { this.userCount -= 1 })
        }
    }

    suspend fun joinGroup(user: AuthUser, groupId: Long, joinCode: String?): JoinGroupResponse {
        return joinGroup(groupId, user.uid, joinCode)
    }

    suspend fun joinGroup(groupId: Long, uid: Long, joinCode: String?): JoinGroupResponse {
        return parZip(
            { groupService.findByIdOrThrow(groupId) },
            { groupUserService.countAllByGroupId(groupId) },
            { groupUserService.findAllByUid(uid).associateBy { it.groupId } },
        ) { group, groupUserCount, myGroupUserInfo ->
            if (group.isHidden && group.joinCode != joinCode) {
                throw InvalidRequestException(ErrorCode.IMPOSSIBLE_TO_JOIN_GROUP_ERROR)
            }

            val groupUser = myGroupUserInfo[groupId]

            when {
                /** 이미 다른 그룹에 속해있는 유저 */
                groupUser == null && myGroupUserInfo.isNotEmpty() -> {
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
                    /** TODO: 그룹원 조인 진행시, locking 필요. */
                    if (groupUserCount.toInt() >= group.userCapacity) {
                        throw InvalidRequestException(ErrorCode.EXCEED_GROUP_USER_COUNT_ERROR)
                    }

                    val createdGroupUser = txTemplates.writer.executes {
                        groupService.saveSync(group.apply { this.userCount += 1 })
                        groupUserService.saveSync(groupId, uid)
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
            {
                groupUserScoreService.findAllByGroupId(groupId)
                    .filterNot { groupUserScore -> groupUserScore.score == null }
                    .sortedBy { groupUserScore -> groupUserScore.score }
                    .take(5)
            }
        ) { group, groupUserScore ->
            val ownerGroupUser = userInfoService.getUserByIdOrThrow(group.ownerUid)

            GetGroupResponse.from(group, ownerGroupUser.nickname).run {
                when (group.ownerUid == user.uid) {
                    true -> this
                    false -> this.copy(joinCode = null)
                }
            }.run {
                when (group.isHidden) {
                    true -> this
                    false -> {
                        val uids = groupUserScore.map { it.uid }

                        val userInfo = userInfoService.findAllByIds(uids).associateBy { userInfo -> userInfo.id }
                        val groupUser = groupUserService.findAllByGroupIdAndUids(groupId, uids)
                            .associateBy { groupUser -> groupUser.uid }

                        val rank = AtomicInteger(1)

                        val ranks = groupUserScore.mapNotNull { score ->
                            GetGroupRankResponse(
                                groupUserId = groupUser[score.uid]?.id ?: return@mapNotNull null,
                                name = userInfo[score.uid]?.nickname ?: return@mapNotNull null,
                                rank = rank.getAndIncrement(),
                                score = score.score ?: 0,
                            )
                        }

                        this.copy(ranks = ranks)
                    }
                }
            }
        }
    }

    suspend fun searchGroup(user: AuthUser, pageRequest: HeroPageRequest): Page<SearchGroupResponse> {
        val groups = groupService.findAll(pageRequest.toDefault())

        val groupUserByUid = groups.content.map { group -> group.id }
            .run { groupUserService.findByUidAndGroupIdIn(user.uid, this) }
            .associateBy { groupUser -> groupUser.groupId }

        return groups
            .map { group ->
                val hasJoined = groupUserByUid[group.id] != null
                SearchGroupResponse.from(group, hasJoined)
            }
    }

    suspend fun deleteGroupUser(user: AuthUser, groupUserId: Long) {
        val groupUser = groupUserService.findByIdOrNull(groupUserId)
            ?: throw NotFoundException(ErrorCode.NOT_FOUND_USER_ERROR)

        val group = groupService.findByIdAndOwnerUid(groupUser.groupId, user.uid)
            ?: throw NotFoundException(ErrorCode.NOT_FOUND_USER_ERROR)

        txTemplates.writer.executesOrNull {
            groupUserService.deleteSync(groupUserId)
            groupService.saveSync(group.apply { this.userCount -= 1 })
            groupUserScoreService.deleteAllByUid(user.uid)
        }
    }

    suspend fun checkGroupRegisterRequest(user: AuthUser, request: CheckGroupRegisterRequest) {
        if (groupService.existsByName(request.name)) {
            throw InvalidRequestException(ErrorCode.DUPLICATE_GROUP_NAME_ERROR)
        }
    }

    suspend fun getGroupRank(user: AuthUser, groupId: Long): GetGroupRanksResponse {
        val groupUser = groupUserService.findByGroupIdAndUid(groupId, user.uid)
            ?: throw InvalidRequestException(ErrorCode.NOT_CONTAINS_GROUP_USER_ERROR)

        val groupUserScores = groupUserScoreService.findAllByGroupId(groupId)
            .filterNot { groupUserScore -> groupUserScore.score == null }
            .sortedBy { groupUserScore -> groupUserScore.score }

        val userById = userInfoService.findAllByIds(groupUserScores.map { it.uid }).associateBy { it.id }

        val rank = AtomicInteger(1)
        val ranks = groupUserScores.mapNotNull { groupUserScore ->
            GetGroupRankResponse(
                groupUserId = groupUserScore.groupUserId,
                name = userById[groupUserScore.uid]?.nickname ?: return@mapNotNull null,
                rank = rank.getAndIncrement(),
                score = groupUserScore.score ?: return@mapNotNull null,
            )
        }
        val avgGroupUserScore = when (ranks.isNotEmpty()) {
            true -> (ranks.sumOf { it.score } / ranks.size)
            false -> null
        }
        val myScore = ranks.firstOrNull { it.groupUserId == groupUser.id }?.score

        return GetGroupRanksResponse(
            groupId = groupUser.groupId,
            ranks = ranks,
            avgScore = avgGroupUserScore,
            myScore = myScore
        )
    }

    suspend fun refreshGroupScore(uid: Long) {
        /** group score 처리 */
        groupUserService.findByUidOrNull(uid)?.run {
            val to = LocalDateTime.now()
            val from = to.minusHours(1)

            val score = poseSnapshotService.countByUidAndModifiedAt(
                uid = uid,
                fromCreatedAt = from,
                toCreatedAt = to
            ).filter { model -> model.type in BAD_POSE }.sumOf { model -> model.count }.toInt()

            val groupUserScore = groupUserScoreService.createOrUpdateGroupUserScore(this, score)

            sendEventWithDelay(groupUserScore)
        }
    }

    private fun sendEventWithDelay(groupUserScore: GroupUserScore) {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            delay(3000)
            wsHandler.launchSendEvent(groupUserScore.uid, groupUserScore.groupId)
        }
    }
}
