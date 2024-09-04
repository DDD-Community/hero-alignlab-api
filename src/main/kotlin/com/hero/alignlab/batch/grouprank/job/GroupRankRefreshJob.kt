package com.hero.alignlab.batch.grouprank.job

import com.hero.alignlab.common.extension.coExecuteOrNull
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

        val counts = poseSnapshotService.countByUidsAndCreatedAtBetween(
            uids = uids,
            fromCreatedAt = from,
            toCreatedAt = to
        ).associateBy { it.uid }

        groupUserScoreService.findAllByUids(uids)
            .groupBy { it.groupId }
            .forEach { (key, value) ->
                val groupUserScore = value.mapNotNull {
                    val score = counts[it.uid]?.count?.toInt() ?: return@mapNotNull null

                    it.apply {
                        it.score = score
                    }
                }

                txTemplates.writer.coExecuteOrNull {
                    groupUserScoreService.saveAllSync(groupUserScore)
                }

                wsHandler.launchSendEvent(key)
            }
    }
}
