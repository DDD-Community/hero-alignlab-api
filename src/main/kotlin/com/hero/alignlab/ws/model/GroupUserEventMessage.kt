package com.hero.alignlab.ws.model

import com.hero.alignlab.common.extension.mapper
import com.hero.alignlab.domain.group.domain.GroupUser
import com.hero.alignlab.domain.group.domain.GroupUserScore
import com.hero.alignlab.domain.user.domain.UserInfo
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger

data class GroupUserEventMessage(
    /**
     * - ws trace를 하기 위한 목적, 향후 해당 필드를 제거한다.
     * - trace의 생성은 보내는 action이 진행되는 곳에서 생성한다.
     */
    val trace: Trace,
    /** ws를 보낸 시간 */
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val groupId: Long,
    /** 본인 정보, 접속 종료시 본인 정보는 미제공 */
    val groupUser: ConcurrentUser?,
    /** 그룹 유저 리스트 */
    val groupUsers: List<ConcurrentUser>,
    val cheerUp: CheerUpModel,
) {
    /** 트래킹을 위해, 해당 변수의 네이밍은 한글로 제공 */
    data class Trace(
        val action: String,
        val rootUid: Long,
        val reason: String,
    )

    data class ConcurrentUser(
        val groupUserId: Long,
        val uid: Long,
        val nickname: String,
        val rank: Int,
        val score: Int,
    )

    data class CheerUpModel(
        /** 나에게 응원하기를 보낸 사용자의 uid */
        val senderUid: Long?,
        /** 금일 받은 응원하기 수 */
        val countCheeredUp: Long,
        /** 내가 응원을 보낸 사용자 목록 */
        val sentUids: List<Long>,
    )

    companion object {
        fun of(
            trace: Trace,
            uid: Long,
            groupId: Long,
            userInfoByUid: Map<Long, UserInfo>,
            groupUserById: Map<Long, GroupUser>,
            scoreByUid: Map<Long, GroupUserScore>,
            cheerUpSenderUid: Long?,
            countCheeredUp: Long,
            cheerUpsByTargetUid: List<Long>,
        ): GroupUserEventMessage {
            val rank = AtomicInteger(1)

            val groupUsers = userInfoByUid.mapNotNull { (uid, info) ->
                val groupUser = groupUserById[uid] ?: return@mapNotNull null

                ConcurrentUser(
                    groupUserId = groupUser.id,
                    uid = uid,
                    nickname = info.nickname,
                    rank = -1,
                    score = scoreByUid[uid]?.score ?: 0,
                )
            }.sortedBy { groupScore ->
                groupScore.score
            }.map { groupScore ->
                groupScore.copy(rank = rank.getAndIncrement())
            }

            return GroupUserEventMessage(
                trace = trace,
                groupId = groupId,
                groupUser = groupUsers.firstOrNull { users -> users.uid == uid },
                groupUsers = groupUsers.take(5),
                cheerUp = CheerUpModel(
                    senderUid = cheerUpSenderUid,
                    countCheeredUp = countCheeredUp,
                    sentUids = cheerUpsByTargetUid,
                )
            )
        }
    }

    fun message(): String {
        return mapper.writeValueAsString(this)
    }
}
