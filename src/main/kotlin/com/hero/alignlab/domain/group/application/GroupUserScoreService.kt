package com.hero.alignlab.domain.group.application

import com.hero.alignlab.domain.group.domain.GroupUserScore
import com.hero.alignlab.domain.group.infrastructure.GroupUserScoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class GroupUserScoreService(
    private val groupUserScoreRepository: GroupUserScoreRepository,
) {
    suspend fun findAllByGroupId(groupId: Long): List<GroupUserScore> {
        return withContext(Dispatchers.IO) {
            groupUserScoreRepository.findAllByGroupId(groupId)
        }
    }
}
