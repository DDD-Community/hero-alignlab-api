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
    suspend fun generateGroupUserMessage(groupId: Long, uids: List<Long>): GroupUserEventMessage {
        return parZip(
            { userInfoService.findAllByIds(uids).associateBy { userInfo -> userInfo.id } },
            { groupUserService.findAllByGroupIdAndUids(groupId, uids).associateBy { groupUser -> groupUser.uid } },
            { groupUserScoreService.findAllByGroupIdAndUids(groupId, uids).associateBy { score -> score.uid } }
        ) { userInfoByUid, groupUsers, groupUserScores ->
            GroupUserEventMessage.of(
                groupId = groupId,
                userInfoByUid = userInfoByUid,
                groupUserss = groupUsers,
                groupUserSocres = groupUserScores
            )
        }
    }
}
