package com.hero.alignlab.domain.pose.resource

import com.hero.alignlab.common.extension.wrapCreated
import com.hero.alignlab.common.extension.wrapOk
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.pose.application.PoseLayoutFacade
import com.hero.alignlab.domain.pose.model.request.PoseLayoutRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@Tag(name = "Pose Layout API")
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class PoseLayoutResource(
    private val poseLayoutFacade: PoseLayoutFacade
) {
    /**
     * - 가장 최근에 생성된 레이아웃 데이터를 조회
     * - 만약, 데이터가 없는 경우, 다음과 같이 데이터를 제공
     * ```
     * {
     *      "id" : -1,
     *      "points": []
     * }
     * ```
     */
    @Operation(summary = "가장 최근 포즈 레이아웃 데이터 조회")
    @GetMapping("/api/v1/pose-layouts/recent")
    suspend fun getRecentPoseLayout(
        user: AuthUser,
    ) = poseLayoutFacade.getRecentPoseLayout(user).wrapOk()

    @Operation(summary = "포즈 레이아웃 조회")
    @GetMapping("/api/v1/pose-layouts/{id}")
    suspend fun getPoseLayout(
        user: AuthUser,
        @PathVariable id: Long,
    ) = poseLayoutFacade.getPoseLayout(user, id).wrapOk()

    @Operation(summary = "포즈 레이아웃 생성")
    @PostMapping("/api/v1/pose-layouts")
    suspend fun register(
        user: AuthUser,
        @RequestBody request: PoseLayoutRequest,
    ) = poseLayoutFacade.register(user, request).wrapCreated()
}
