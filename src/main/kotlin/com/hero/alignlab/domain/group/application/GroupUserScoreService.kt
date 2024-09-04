package com.hero.alignlab.domain.group.application

import com.hero.alignlab.common.extension.coExecuteOrNull
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.group.domain.GroupUser
import com.hero.alignlab.domain.group.domain.GroupUserScore
import com.hero.alignlab.domain.group.infrastructure.GroupUserScoreRepository
import com.hero.alignlab.ws.handler.ReactiveGroupUserWebSocketHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GroupUserScoreService(
    private val groupUserScoreRepository: GroupUserScoreRepository,
    private val txTemplates: TransactionTemplates,
    private val wsHandler: ReactiveGroupUserWebSocketHandler,
) {
    suspend fun findAllByGroupId(groupId: Long): List<GroupUserScore> {
        return withContext(Dispatchers.IO) {
            groupUserScoreRepository.findAllByGroupId(groupId)
        }
    }

    suspend fun findByUidOrNull(uid: Long): GroupUserScore? {
        return withContext(Dispatchers.IO) {
            groupUserScoreRepository.findByUid(uid)
        }
    }

    suspend fun findAllByGroupUserIdIn(groupdUserIds: List<Long>): List<GroupUserScore> {
        return withContext(Dispatchers.IO) {
            groupUserScoreRepository.findAllByGroupUserIdIn(groupdUserIds)
        }
    }

    @Transactional
    fun saveSync(groupUserScore: GroupUserScore): GroupUserScore {
        return groupUserScoreRepository.save(groupUserScore)
    }

    @Transactional
    fun saveAllSync(groupUserScores: List<GroupUserScore>): List<GroupUserScore> {
        return groupUserScoreRepository.saveAll(groupUserScores)
    }

    suspend fun findAllByGroupIdAndUids(groupId: Long, uids: List<Long>): List<GroupUserScore> {
        return withContext(Dispatchers.IO) {
            findAllByGroupIdAndUidsSync(groupId, uids)
        }
    }

    fun findAllByGroupIdAndUidsSync(groupId: Long, uids: List<Long>): List<GroupUserScore> {
        return groupUserScoreRepository.findAllByGroupIdAndUidIn(groupId, uids)
    }

    suspend fun deleteAll() {
        txTemplates.writer.coExecuteOrNull {
            groupUserScoreRepository.deleteAllInBatch()
        }
    }

    suspend fun createOrUpdateGroupUserScore(groupUser: GroupUser, score: Int) {
        val groupUserScore = findByUidOrNull(groupUser.uid)

        val createOrUpdateGroupUserScore = when (groupUserScore == null) {
            true -> GroupUserScore(
                groupId = groupUser.groupId,
                groupUserId = groupUser.id,
                uid = groupUser.uid,
                score = score
            )

            false -> groupUserScore.apply {
                this.score = score
            }
        }

        txTemplates.writer.coExecuteOrNull {
            saveSync(createOrUpdateGroupUserScore)
        }

        wsHandler.launchSendEvent(groupUser.groupId)
    }

    suspend fun findAllByUids(uids: List<Long>): List<GroupUserScore> {
        return withContext(Dispatchers.IO) {
            groupUserScoreRepository.findAllByUidIn(uids)
        }
    }
}
