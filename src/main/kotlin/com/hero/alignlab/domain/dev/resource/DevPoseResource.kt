package com.hero.alignlab.domain.dev.resource

import com.hero.alignlab.batch.posecount.job.PoseCountUpdateJob
import com.hero.alignlab.common.extension.wrapVoid
import com.hero.alignlab.config.swagger.SwaggerTag.DEV_TAG
import com.hero.alignlab.domain.auth.model.DevAuthUser
import com.hero.alignlab.domain.dev.application.DevPoseService
import com.hero.alignlab.domain.dev.model.request.DevPoseSnapshotRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Tag(name = DEV_TAG)
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class DevPoseResource(
    private val devPoseService: DevPoseService,
    private val poseCountUpdateJob: PoseCountUpdateJob,
) {
    @Operation(summary = "[DEV] 포즈 스냅샷 생성")
    @PostMapping("/api/dev/v1/pose-snapshots")
    suspend fun createPoseSnapshots(
        dev: DevAuthUser,
        @RequestBody request: DevPoseSnapshotRequest,
    ): ResponseEntity<Unit> {
        return devPoseService.create(request).wrapVoid()
    }

    @Operation(summary = "[DEV] 포즈 데이터 삭제")
    @DeleteMapping("/api/dev/v1/pose-snapshots/{id}")
    suspend fun deletePoseSnapshots(
        dev: DevAuthUser,
    ) {
        devPoseService.deleteAllPoseData()
    }

    @Operation(summary = "[DEV] 포즈 데이터 통계 처리")
    @PostMapping("/api/dev/v1/pose-counts")
    suspend fun updatePoseCounts(
        dev: DevAuthUser,
        @RequestParam targetDate: LocalDate
    ) {
        poseCountUpdateJob.run(targetDate)
    }
}
