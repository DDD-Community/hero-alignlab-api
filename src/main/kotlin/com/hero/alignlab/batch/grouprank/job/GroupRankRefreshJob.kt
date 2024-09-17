package com.hero.alignlab.batch.grouprank.job

import com.hero.alignlab.common.extension.coExecute
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.group.application.GroupUserScoreService
import com.hero.alignlab.domain.group.application.GroupUserService
import com.hero.alignlab.domain.pose.application.PoseSnapshotService
import com.hero.alignlab.ws.handler.ReactiveGroupUserWebSocketHandler
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class GroupRankRefreshJob(
    private val groupUserService: GroupUserService,
    private val poseSnapshotService: PoseSnapshotService,
    private val groupUserScoreService: GroupUserScoreService,
    private val txTemplates: TransactionTemplates,
    private val wsHandler: ReactiveGroupUserWebSocketHandler,
) {
    suspend fun run() {
        val to = LocalDateTime.now()
        val from = to.minusHours(1)

        val groupUsers = groupUserService.findAll()

        val uids = groupUsers.map { it.uid }

        val counts = poseSnapshotService.countByUidsAndModifiedAtBetween(
            uids = uids,
            fromModifiedAt = from,
            toModifiedAt = to
        ).associateBy { it.uid }

        /** 랭크 업데이트 */
        groupUserScoreService.findAllByUids(uids)
            .groupBy { groupUserScore -> groupUserScore.groupId }
            .forEach { (groupId, scores) ->
                val groupUserScores = scores.mapNotNull { groupUserScore ->
                    val score = counts[groupUserScore.uid]?.count?.toInt() ?: return@mapNotNull null

                    groupUserScore.apply {
                        groupUserScore.score = score
                    }
                }

                val updatedGroupUserScores = txTemplates.writer.coExecute {
                    groupUserScoreService.saveAllSync(groupUserScores)
                }

                updatedGroupUserScores.forEach { groupUserScore ->
                    wsHandler.launchSendEvent(groupUserScore.uid, groupUserScore.groupId)
                }
            }

        // TODO : 개발 작업 진행을 위해 해당 로직 주석 처리
/*        *//** 1시간 전에 생성된 랭크 정보는 삭제 *//*
        txTemplates.writer.coExecute {
            groupUserScoreService.deleteAllByModifiedAtBeforeSync(from)
        }*/
    }
}
