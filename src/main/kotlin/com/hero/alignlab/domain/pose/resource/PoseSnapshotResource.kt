package com.hero.alignlab.domain.pose.resource

import com.hero.alignlab.common.extension.wrapCreated
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.pose.application.PoseSnapshotFacade
import com.hero.alignlab.domain.pose.model.request.PoseSnapshotRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Pose Snapshot API")
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class PoseSnapshotResource(
    private val poseSnapshotFacade: PoseSnapshotFacade,
) {
    @Operation(summary = "pose snapshot 저장")
    @PostMapping("/api/v1/pose-snapshots")
    suspend fun loadPoseSnapshot(
        user: AuthUser,
        @RequestBody request: PoseSnapshotRequest,
    ) = poseSnapshotFacade.loadPoseSnapshot(user, request).wrapCreated()
}
