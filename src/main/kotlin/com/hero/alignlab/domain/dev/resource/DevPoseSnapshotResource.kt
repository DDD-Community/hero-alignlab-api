package com.hero.alignlab.domain.dev.resource

import com.hero.alignlab.common.extension.wrapVoid
import com.hero.alignlab.config.dev.DevResourceCheckConfig.Companion.devResource
import com.hero.alignlab.config.swagger.SwaggerTag.DEV_TAG
import com.hero.alignlab.domain.dev.application.DevPoseSnapshotService
import com.hero.alignlab.domain.dev.model.request.DevPoseSnapshotRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@Tag(name = DEV_TAG)
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class DevPoseSnapshotResource(
    private val devPoseSnapshotService: DevPoseSnapshotService,
) {
    @Operation(summary = "[DEV] 포즈 스냅샷 생성")
    @PostMapping("/api/dev/v1/pose-snapshots")
    suspend fun createPoseSnapshots(
        @RequestHeader("X-HERO-DEV-TOKEN") token: String,
        @RequestBody request: DevPoseSnapshotRequest,
    ) = devResource(token) {
        devPoseSnapshotService.create(request).wrapVoid()
    }
}
