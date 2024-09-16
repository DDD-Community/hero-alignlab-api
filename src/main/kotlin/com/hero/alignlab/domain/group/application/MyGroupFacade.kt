package com.hero.alignlab.domain.group.application

import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.group.model.response.MyGroupResponse
import com.hero.alignlab.domain.user.application.UserInfoService
import org.springframework.stereotype.Service

@Service
class MyGroupFacade(
    private val groupService: GroupService,
    private val groupUserService: GroupUserService,
    private val userInfoService: UserInfoService,
) {
    // TODO : 기획 확인후, 코드 변경
    suspend fun getMyGroup(user: AuthUser): MyGroupResponse? {
        val groupUser = groupUserService.findByUidOrNull(user.uid) ?: return null
        val group = groupService.findByIdOrThrow(groupUser.groupId)
        val groupUserCount = groupUserService.countAllByGroupId(groupUser.groupId)
        val ownerUserInfo = userInfoService.findByIdOrThrow(group.ownerUid)

        return MyGroupResponse.of(group, groupUserCount.toInt(), ownerUserInfo.nickname)
    }
}
