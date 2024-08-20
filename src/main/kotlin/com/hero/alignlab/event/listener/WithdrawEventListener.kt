package com.hero.alignlab.event.listener

import com.hero.alignlab.domain.group.application.GroupFacade
import com.hero.alignlab.domain.group.infrastructure.GroupRepository
import com.hero.alignlab.domain.notification.infrastructure.PoseNotificationRepository
import com.hero.alignlab.domain.pose.infrastructure.*
import com.hero.alignlab.event.model.WithdrawEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class WithdrawEventListener(
    /** 탈퇴 회원이 그룹장인 경우 승계 작업 필요. */
    private val groupFacade: GroupFacade,
    private val groupRepository: GroupRepository,

    /** 자세 데이터, 탈퇴회원이 있더라도 문제 없음. */
    private val poseCountRepository: PoseCountRepository,
    private val poseSnapshotRepository: PoseSnapshotRepository,
    private val poseKeyPointSnapshotRepository: PoseKeyPointSnapshotRepository,
    private val poseLayoutRepository: PoseLayoutRepository,
    private val poseLayoutPointRepository: PoseLayoutPointRepository,
    private val poseNotificationRepository: PoseNotificationRepository,
) {
    /**
     * 현상황에서 그룹 승계 작업만 원활히 진행되면, 그외 데이터의 경우 탈퇴로 인한 문제는 발생하지 않는다.
     */
    @TransactionalEventListener
    fun handle(event: WithdrawEvent) {
        /** 그룹 승계 및 탈퇴 */
        CoroutineScope(Dispatchers.IO).launch {
            val group = groupRepository.findByOwnerUid(event.uid)

            if (group != null) {
                groupFacade.withdraw(group.id, event.uid)
            }
        }
    }
}
