package com.hero.alignlab.batch.grouprank.job

import com.hero.alignlab.domain.group.application.GroupFacade
import com.hero.alignlab.domain.group.application.GroupUserService
import org.springframework.stereotype.Component

@Component
class GroupRankRefreshJob(
    private val groupUserService: GroupUserService,
    private val groupFacade: GroupFacade,
) {
    suspend fun run() {
        val groupUsers = groupUserService.findAll()

        groupUsers.forEach { groupUser ->
            groupFacade.refreshGroupScore(groupUser.uid)
        }
    }
}
