package com.hero.alignlab.domain.dev.resource

import com.hero.alignlab.batch.grouprank.job.GroupRankRefreshJob
import com.hero.alignlab.config.swagger.SwaggerTag.DEV_TAG
import com.hero.alignlab.domain.auth.model.DevAuthUser
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = DEV_TAG)
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class DevGroupScoreResource(
    private val groupRankRefreshJob: GroupRankRefreshJob,
) {
    @Operation(summary = "group score 갱신")
    @PostMapping("/api/dev/v1/group-scores")
    suspend fun reloadGroupScores(
        dev: DevAuthUser,
    ) {
        groupRankRefreshJob.run()
    }
}
