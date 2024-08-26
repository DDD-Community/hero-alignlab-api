package com.hero.alignlab.domain.pose.resource

import com.hero.alignlab.common.extension.wrapOk
import com.hero.alignlab.common.extension.wrapPage
import com.hero.alignlab.common.model.HeroPageRequest
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.pose.application.PoseCountService
import com.hero.alignlab.domain.pose.model.request.PoseSearchRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@Tag(name = "Pose API")
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class PoseCountResource(
    private val poseCountService: PoseCountService,
) {
    /**
     * 정렬
     * - date,desc
     * - date,asc
     */
    @Operation(summary = "pose 통계 정보 검색")
    @GetMapping("/api/v1/pose-counts")
    suspend fun search(
        user: AuthUser,
        @ParameterObject request: PoseSearchRequest,
        @ParameterObject pageRequest: HeroPageRequest,
    ) = poseCountService.search(
        user = user,
        request = request,
        pageRequest = pageRequest
    ).wrapPage()

    /** date 조건이 없는 경우, 현재 시간을 기준으로 조회 */
    @Operation(summary = "daily pose 정보 조회")
    @GetMapping("/api/v1/pose-counts/daily")
    suspend fun dailyPoseCount(
        user: AuthUser,
        @RequestParam(required = false) date: LocalDate?
    ) = poseCountService.getDailyPoseCount(user, date).wrapOk()
}
