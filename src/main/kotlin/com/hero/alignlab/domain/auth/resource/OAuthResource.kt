package com.hero.alignlab.domain.auth.resource

import com.hero.alignlab.common.extension.wrapOk
import com.hero.alignlab.domain.auth.application.OAuthFacade
import com.hero.alignlab.domain.auth.model.OAuthProvider
import com.hero.alignlab.domain.auth.model.request.OAuthAuthorizedRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

/**
 * - [Kakao Rest Auth](https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api)
 */
@Tag(name = "OAuth 인증 및 인가 관리")
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class OAuthResource(
    private val oAuthFacade: OAuthFacade,
) {
    /**
     * OAuth로 인가코드를 부여받고, 이를 Redirect Url로 반환.
     * - redirectUrl은 Client의 주소값, 만약 변경시 yml 및 각 클라이언트 구조 변경 필요.
     */
    @Operation(summary = "인가 코드 받기")
    @PostMapping("/api/v1/oauth/{provider}/authorize")
    suspend fun getOAuthAuthorizeCode(
        @PathVariable provider: OAuthProvider,
    ) = oAuthFacade.getOAuthAuthorizeCode(provider).wrapOk()

    @Operation(summary = "회원가입 또는 로그인 진행")
    @PostMapping("/api/v1/oauth/{provider}/authorized")
    suspend fun resolveOAuth(
        @PathVariable provider: OAuthProvider,
        @RequestBody request: OAuthAuthorizedRequest,
    ) = oAuthFacade.resolveOAuth(provider, request).wrapOk()
}
