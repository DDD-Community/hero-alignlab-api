package com.hero.alignlab.domain.auth.resource

import com.hero.alignlab.domain.auth.application.OAuthFacade
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

}
