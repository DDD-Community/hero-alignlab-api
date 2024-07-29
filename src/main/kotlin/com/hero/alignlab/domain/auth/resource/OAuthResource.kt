package com.hero.alignlab.domain.auth.resource

import com.hero.alignlab.common.extension.wrapOk
import com.hero.alignlab.domain.auth.application.OAuthFacade
import com.hero.alignlab.domain.auth.model.OAuthProvider
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "OAuth 인증 및 인가 관리")
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class OAuthResource(
    private val oAuthFacade: OAuthFacade,
) {
    /**
     * OAuth로 인가코드를 부여받고, 이를 Redirect Url로 반환.
     * - redirectUrl은 Client의 주소값, 만약 변경시 yml 및 각 클라이언트 구조 변경 필요.
     * - [Kakao Rest Auth](https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api)
     */
    @Operation(summary = "인가 코드 받기")
    @GetMapping("/api/v1/oauth/{provider}/authorize")
    suspend fun getOAuthAuthorizeCode(
        @RequestParam provider: OAuthProvider,
    ) = oAuthFacade.getOAuthAuthorizeCode(provider).wrapOk()
}
