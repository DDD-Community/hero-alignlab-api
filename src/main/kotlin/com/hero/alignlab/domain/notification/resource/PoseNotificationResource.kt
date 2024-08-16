package com.hero.alignlab.domain.notification.resource

import com.hero.alignlab.common.extension.wrapCreated
import com.hero.alignlab.common.extension.wrapOk
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.notification.application.PoseNotificationService
import com.hero.alignlab.domain.notification.model.request.PatchPoseNotificationRequest
import com.hero.alignlab.domain.notification.model.request.RegisterPoseNotificationRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@Tag(name = "Pose Noti API")
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class PoseNotificationResource(
    private val poseNotificationService: PoseNotificationService,
) {
    /**
     * **저장된 자세 알림 정보를 조회**
     * - 활성화된 데이터가 있는 경우에만, 데이터를 제공
     * - 비활성화된 경우에는, 빈 데이터를 제공
     * - duration
     *      - IMMEDIATELY : 즉시
     *      - MIN_15 : 15분
     *      - MIN_30 : 30분
     *      - MIN_45 : 45분
     *      - MIN_60 : 60분
     */
    @Operation(summary = "자세 알림 조회")
    @GetMapping(path = ["/api/v1/pose-notifications"])
    suspend fun getPoseNotification(
        user: AuthUser,
    ) = poseNotificationService.getNotification(user).wrapOk()

    @Operation(summary = "자세 알림 등록 또는 수정")
    @PostMapping(path = ["/api/v1/pose-notifications"])
    suspend fun registerPoseNotification(
        user: AuthUser,
        @RequestBody request: RegisterPoseNotificationRequest,
    ) = poseNotificationService.registerNotification(user, request).wrapCreated()

    /** 변경이 필요한 항목만 Request로 입력 */
    @Operation(summary = "자세 알림 활성화, 주기 정보 변경")
    @PatchMapping(path = ["/api/v1/pose-notifications/{id}"])
    suspend fun patchPoseNotification(
        user: AuthUser,
        @RequestBody request: PatchPoseNotificationRequest,
    ) = poseNotificationService.patch(user, request).wrapOk()
}
