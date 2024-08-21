package com.hero.alignlab.ws.model

import com.hero.alignlab.domain.group.domain.GroupUser
import com.hero.alignlab.domain.group.domain.GroupUserScore
import com.hero.alignlab.domain.user.domain.UserInfo
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger

data class ConcurrentMessage(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val groupId: Long,
    val groupUsers: List<ConcurrentUser>
) {
    data class ConcurrentUser(
        val groupUserId: Long,
        val uid: Long,
        val nickname: String,
        val rank: Int,
        val score: Int,
    )

    companion object {
        fun of(
            groupId: Long,
            userInfoByUid: Map<Long, UserInfo>,
            groupUserss: Map<Long, GroupUser>,
            groupUserSocres: Map<Long, GroupUserScore>
        ): ConcurrentMessage {
            val rank = AtomicInteger(1)

            return ConcurrentMessage(
                groupId = groupId,
                groupUsers = userInfoByUid.mapNotNull { (uid, info) ->
                    val groupUSer = groupUserss[uid] ?: return@mapNotNull null

                    ConcurrentUser(
                        groupUserId = groupUSer.id,
                        uid = uid,
                        nickname = info.nickname,
                        rank = rank.getAndIncrement(),
                        score = groupUserSocres[uid]?.score ?: 0,
                    )
                }
            )
        }
    }
}
