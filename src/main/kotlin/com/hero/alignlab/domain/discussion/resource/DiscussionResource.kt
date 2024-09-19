package com.hero.alignlab.domain.discussion.resource

import com.hero.alignlab.common.extension.wrapCreated
import com.hero.alignlab.common.model.Response
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.discussion.application.DiscussionService
import com.hero.alignlab.domain.discussion.model.request.DiscussionRequest
import com.hero.alignlab.domain.discussion.model.response.DiscussionResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
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
    /**
     * - type : QNA(문의하기), REPORT(신고하기), SUGGEST(제안하기)
     */
    @Operation(summary = "문의하기 생성")
    @PostMapping("/api/v1/discussion")
    suspend fun create(
        user: AuthUser,
        @RequestBody request: DiscussionRequest,
    ): ResponseEntity<Response<DiscussionResponse>> {
        return discussionService.create(user, request).wrapCreated()
    }
}
