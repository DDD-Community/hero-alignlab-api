package com.hero.alignlab.domain.cheer.application

import arrow.fx.coroutines.parZip
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.cheer.domain.CheerUp
import com.hero.alignlab.domain.cheer.model.request.CheerUpRequest
import com.hero.alignlab.domain.cheer.model.response.CheerUpResponse
import com.hero.alignlab.domain.cheer.model.response.CheerUpSummaryResponse
import com.hero.alignlab.domain.group.application.GroupUserService
import com.hero.alignlab.ws.handler.ReactiveGroupUserWebSocketHandler
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CheerUpFacade(
    private val groupUserService: GroupUserService,
    private val cheerUpService: CheerUpService,
    private val reactiveGroupUserWebSocketHandler: ReactiveGroupUserWebSocketHandler,
) {
    private val logger = KotlinLogging.logger { }

    suspend fun cheerUp(user: AuthUser, request: CheerUpRequest): CheerUpResponse {
        val now = LocalDate.now()

        /** 동일 그룹 유저인지 확인하기 */
        val groupUser = groupUserService.findByUidOrThrow(user.uid)
        val otherGroupUsers = groupUserService.findAllByGroupId(groupUser.groupId)

        val createdUids = otherGroupUsers.mapNotNull { otherGroupUser ->
            val isSameGroup = request.uids.contains(otherGroupUser.uid)
            val isExists = cheerUpService.existsByUidAndTargetUidAndCheeredAt(
                uid = user.uid,
                targetUid = otherGroupUser.uid,
                cheeredAt = now
            )

            /** 동일 그룹, 응원하기를 진행하지 않는 경우에 action 진행 */
            when (isSameGroup && !isExists) {
                true -> {
                    runCatching {
                        cheerUpService.save(
                            CheerUp(
                                uid = user.uid,
                                targetUid = otherGroupUser.uid,
                                cheeredAt = now
                            )
                        )
                    }.onFailure { e ->
                        /** 이미 등록된 케이스인 경우가 대다수 */
                        logger.error(e) { "fail to create cheerUp" }
                    }.onSuccess { createdCheerUp ->
                        reactiveGroupUserWebSocketHandler.launchSendEventByCheerUp(
                            actorUid = user.uid,
                            targetUid = createdCheerUp.targetUid,
                            groupId = otherGroupUser.groupId,
                        )
                    }.getOrNull()?.targetUid
                }

                /** 응원하기를 진행하지 못한 경우 */
                false -> null
            }
        }

        return CheerUpResponse(createdUids.toSet())
    }

    suspend fun getCheerUpSummary(user: AuthUser, cheeredAt: LocalDate): CheerUpSummaryResponse {
        return parZip(
            { cheerUpService.countAllByCheeredAtAndUid(cheeredAt, user.uid) },
            { cheerUpService.findAllByUidAndCheeredAt(user.uid, cheeredAt) }
        ) { countCheeredUp, cheerUps ->
            CheerUpSummaryResponse(
                countCheeredUp = countCheeredUp,
                cheeredUpUids = cheerUps.map { cheerUp -> cheerUp.targetUid }
            )
        }
    }
}
