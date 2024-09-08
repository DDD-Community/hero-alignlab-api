package com.hero.alignlab.ws.service

import arrow.fx.coroutines.parZip
import com.hero.alignlab.domain.group.application.GroupUserScoreService
import com.hero.alignlab.domain.group.application.GroupUserService
import com.hero.alignlab.domain.user.application.UserInfoService
import com.hero.alignlab.ws.model.GroupUserEventMessage
import org.springframework.stereotype.Service

@Service
class GroupUserWsFacade(
    private val userInfoService: UserInfoService,
    private val groupUserService: GroupUserService,
    private val groupUserScoreService: GroupUserScoreService,
) {
    suspend fun generateEventMessage(uid: Long, groupId: Long, uids: List<Long>): GroupUserEventMessage {
        return parZip(
            { userInfoService.findAllByIds(uids) },
            { groupUserService.findAllByGroupIdAndUids(groupId, uids) },
            { groupUserScoreService.findAllByGroupIdAndUids(groupId, uids) }
        ) { userInfoByUid, groupUsers, groupUserScores ->
            GroupUserEventMessage.of(
                uid = uid,
                groupId = groupId,
                userInfoByUid = userInfoByUid.associateBy { userInfo -> userInfo.id },
                groupUserById = groupUsers.associateBy { groupUser -> groupUser.uid },
                scoreByUid = groupUserScores.associateBy { score -> score.uid }
            )
        }
    }
}
