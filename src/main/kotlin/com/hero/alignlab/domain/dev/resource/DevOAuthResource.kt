package com.hero.alignlab.domain.dev.resource

import com.hero.alignlab.common.extension.wrapOk
import com.hero.alignlab.domain.auth.model.OAuthProvider
import com.hero.alignlab.domain.dev.application.DevOAuthService
import com.hero.alignlab.domain.dev.model.request.DevRedirectedRequest
import com.hero.alignlab.domain.dev.model.response.DevRedirectedResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Dev OAuth 인증 및 인가 관리")
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class DevOAuthResource(
    private val devOAuthService: DevOAuthService,
) {
    @Operation(summary = "인가 코드 받기")
    @GetMapping("/api/dev/v1/oauth/{provider}/authorize")
    suspend fun getDevOAuthAuthorizeCode(
        @PathVariable provider: OAuthProvider,
    ) = devOAuthService.getOAuthAuthorizeCode(provider).wrapOk()

    @Operation(summary = "OAuth Redirect Test")
    @GetMapping("/api/dev/v1/oauth/{provider}/authorize/redirected")
    suspend fun redirectedDevOAuthAuthorizeCode(
        @PathVariable provider: OAuthProvider,
        @ParameterObject request: DevRedirectedRequest,
    ) = DevRedirectedResponse(
        provider = provider,
        requestParams = request,
    ).wrapOk()
}
