package com.hero.alignlab.domain.group.application

import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.group.model.response.MyGroupResponse
import org.springframework.stereotype.Service

@Service
class MyGroupService(
    private val groupService: GroupService,
    private val groupUserService: GroupUserService,
) {
    suspend fun getMyGroup(user: AuthUser): MyGroupResponse? {
        val groupUser = groupUserService.findByUid(user.uid) ?: return null
        val group = groupService.findByIdOrThrow(groupUser.groupId)

        return MyGroupResponse.from(group)
    }
}
