package com.hero.alignlab.domain.dev.resource

import com.hero.alignlab.client.kakao.model.response.GenerateKakaoOAuthTokenResponse
import com.hero.alignlab.client.kakao.model.response.KakaoOAuthUserInfoResponse
import com.hero.alignlab.common.extension.wrapOk
import com.hero.alignlab.common.model.Response
import com.hero.alignlab.config.swagger.SwaggerTag.DEV_TAG
import com.hero.alignlab.domain.auth.model.OAuthProvider
import com.hero.alignlab.domain.dev.application.DevOAuthService
import com.hero.alignlab.domain.dev.model.response.DevOAuthCodeResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = DEV_TAG)
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class DevOAuthResource(
    private val devOAuthService: DevOAuthService,
) {
    @Operation(summary = "[DEV] 인가 코드 받기")
    @GetMapping("/api/dev/v1/oauth/{provider}/authorize")
    suspend fun getDevOAuthAuthorizeCode(
        @PathVariable provider: OAuthProvider,
    ): ResponseEntity<Response<DevOAuthCodeResponse>> {
        return devOAuthService.getOAuthAuthorizeCode(provider).wrapOk()
    }

    @Operation(summary = "[DEV] OAuth Token Generate")
    @GetMapping("/api/dev/v1/oauth/{provider}/token")
    suspend fun redirectedDevOAuthAuthorizeCode(
        @PathVariable provider: OAuthProvider,
        @RequestParam code: String,
    ): ResponseEntity<Response<GenerateKakaoOAuthTokenResponse>> {
        return devOAuthService.resolveOAuth(provider, code).wrapOk()
    }

    @Operation(summary = "[DEV] 사용자 정보 조회")
    @GetMapping("/api/dev/v1/oauth/user")
    suspend fun getOAuthUserInfos(
        @RequestParam accessToken: String
    ): ResponseEntity<Response<KakaoOAuthUserInfoResponse>> {
        return devOAuthService.getUserInfo(accessToken).wrapOk()
    }

    @Operation(summary = "[DEV] 회원 탈퇴")
    @GetMapping("/api/dev/v1/oauth/{provider}/withdraw")
    suspend fun withdraw(
        @PathVariable provider: OAuthProvider,
        @RequestParam accessToken: String,
        @RequestParam oauthId: String,
    ): ResponseEntity<Response<Boolean>> {
        return devOAuthService.withdraw(
            provider = provider,
            accessToken = accessToken,
            oauthId = oauthId
        ).wrapOk()
    }
}
