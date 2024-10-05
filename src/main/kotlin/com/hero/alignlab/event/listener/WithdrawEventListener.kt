package com.hero.alignlab.event.listener

import com.hero.alignlab.domain.dev.application.DevDeleteService
import com.hero.alignlab.domain.group.application.GroupFacade
import com.hero.alignlab.domain.group.application.GroupService
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
    private val groupService: GroupService,
    private val devDeleteService: DevDeleteService,
) {
    /**
     * 현상황에서 그룹 승계 작업만 원활히 진행되면, 그외 데이터의 경우 탈퇴로 인한 문제는 발생하지 않는다.
     */
    @TransactionalEventListener
    fun handle(event: WithdrawEvent) {
        /** 그룹 승계 및 탈퇴 */
        CoroutineScope(Dispatchers.IO).launch {
            val group = groupService.findByOwnerUid(event.uid)

            if (group != null) {
                groupFacade.withdraw(group.id, event.uid)
            }

            devDeleteService.deleteAll(event.uid)
        }
    }
}
