package com.hero.alignlab.domain.cheer.resource

import com.hero.alignlab.common.extension.wrapCreated
import com.hero.alignlab.common.extension.wrapOk
import com.hero.alignlab.common.model.Response
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.cheer.application.CheerUpFacade
import com.hero.alignlab.domain.cheer.model.request.CheerUpRequest
import com.hero.alignlab.domain.cheer.model.response.CheerUpResponse
import com.hero.alignlab.domain.cheer.model.response.CheerUpSummaryResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Tag(name = "응원하기 API")
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class CheerUpResource(
    private val cheerUpFacade: CheerUpFacade,
) {
    @Operation(summary = "응원하기")
    @PostMapping("/api/v1/cheer-up")
    suspend fun cheerUp(
        user: AuthUser,
        @RequestBody request: CheerUpRequest,
    ): ResponseEntity<Response<CheerUpResponse>> {
        return cheerUpFacade.cheerUp(user, request).wrapCreated()
    }

    @Operation(summary = "본인 응원하기 정보 조회")
    @GetMapping("/api/v1/cheer-up/me")
    suspend fun getMyCheerUp(
        user: AuthUser,
        @RequestParam cheeredAt: LocalDate,
    ): ResponseEntity<Response<CheerUpSummaryResponse>> {
        return cheerUpFacade.getCheerUpSummary(user, cheeredAt).wrapOk()
    }
}
