package com.hero.alignlab.domain.dev.resource

import com.hero.alignlab.common.extension.wrapOk
import com.hero.alignlab.domain.auth.model.OAuthProvider
import com.hero.alignlab.domain.auth.model.request.OAuthAuthorizedRequest
import com.hero.alignlab.domain.dev.application.DevOAuthService
import com.hero.alignlab.domain.dev.model.request.DevRedirectedRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@Tag(name = "Dev OAuth 인증 및 인가 관리")
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class DevOAuthResource(
    private val devOAuthService: DevOAuthService,
) {
    @Operation(summary = "인가 코드 받기")
    @PostMapping("/api/dev/v1/oauth/{provider}/authorize")
    suspend fun getDevOAuthAuthorizeCode(
        @PathVariable provider: OAuthProvider,
    ) = devOAuthService.getOAuthAuthorizeCode(provider).wrapOk()

    @Operation(summary = "OAuth Redirect Test")
    @GetMapping("/api/dev/v1/oauth/{provider}/authorize/redirected")
    suspend fun redirectedDevOAuthAuthorizeCode(
        @PathVariable provider: OAuthProvider,
        @ParameterObject request: DevRedirectedRequest,
    ) = devOAuthService.redirectTest(provider, request).wrapOk()

    @Operation(summary = "OAuth Token Generate")
    @PostMapping("/api/dev/v1/oauth/{provider}/token")
    suspend fun redirectedDevOAuthAuthorizeCode(
        @PathVariable provider: OAuthProvider,
        @RequestBody request: OAuthAuthorizedRequest,
    ) = devOAuthService.resolveOAuth(provider, request).wrapOk()
}
