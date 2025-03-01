package com.hero.alignlab.ws.service

import arrow.fx.coroutines.parZip
import com.hero.alignlab.domain.cheer.application.CheerUpService
import com.hero.alignlab.domain.group.application.GroupUserScoreService
import com.hero.alignlab.domain.group.application.GroupUserService
import com.hero.alignlab.domain.user.application.UserInfoService
import com.hero.alignlab.ws.model.GroupUserEventMessage
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class GroupUserWsFacade(
    private val userInfoService: UserInfoService,
    private val groupUserService: GroupUserService,
    private val groupUserScoreService: GroupUserScoreService,
    private val cheerUpService: CheerUpService,
) {
    suspend fun generateEventMessage(
        trace: GroupUserEventMessage.Trace,
        uid: Long,
        groupId: Long,
        spreadUids: List<Long>,
        cheerUpSenderUid: Long? = null,
    ): GroupUserEventMessage {
        val now = LocalDate.now()

        return parZip(
            { userInfoService.findAllByIds(spreadUids) },
            { groupUserService.findAllByGroupIdAndUids(groupId, spreadUids) },
            { groupUserScoreService.findAllByGroupIdAndUids(groupId, spreadUids) },
            { cheerUpService.countAllByCheeredAtAndUid(now, uid) },
            { cheerUpService.findAllByUidAndCheeredAt(uid, now) },
        ) { userInfoByUid, groupUsers, groupUserScores, countCheeredUp, cheerUps ->
            GroupUserEventMessage.of(
                trace = trace,
                uid = uid,
                groupId = groupId,
                userInfoByUid = userInfoByUid.associateBy { userInfo -> userInfo.id },
                groupUserById = groupUsers.associateBy { groupUser -> groupUser.uid },
                scoreByUid = groupUserScores.associateBy { score -> score.uid },
                cheerUpSenderUid = cheerUpSenderUid,
                countCheeredUp = countCheeredUp,
                cheerUpsByTargetUid = cheerUps.map { cheerUp -> cheerUp.targetUid }
            )
        }
    }
}
