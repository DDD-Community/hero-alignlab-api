package com.hero.alignlab.domain.group.application

import com.hero.alignlab.domain.group.domain.GroupUserScore
import com.hero.alignlab.domain.group.infrastructure.GroupUserScoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GroupUserScoreService(
    private val groupUserScoreRepository: GroupUserScoreRepository,
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

    fun findAllByGroupIdAndUidsSync(groupId: Long, uids: Set<Long>): List<GroupUserScore> {
        return groupUserScoreRepository.findAllByGroupIdAndUidIn(groupId, uids)
    }
}
