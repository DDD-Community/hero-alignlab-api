package com.hero.alignlab.domain.discussion.resource

import com.hero.alignlab.common.extension.wrapCreated
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.discussion.application.DiscussionService
import com.hero.alignlab.domain.discussion.model.request.DiscussionRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "문의하기 API")
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class DiscussionResource(
    private val discussionService: DiscussionService,
) {
    @Operation(summary = "문의하기 생성")
    @PostMapping("/api/v1/discussion")
    suspend fun create(
        user: AuthUser,
        @RequestBody request: DiscussionRequest,
    ) = discussionService.create(user, request).wrapCreated()
}
