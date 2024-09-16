package com.hero.alignlab.domain.group.resource

import com.hero.alignlab.common.extension.wrapOk
import com.hero.alignlab.common.model.Response
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.group.application.MyGroupFacade
import com.hero.alignlab.domain.group.model.response.MyGroupResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "My Group API")
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class MyGroupResource(
    private val myGroupFacade: MyGroupFacade,
) {
    /** 그룹이 없는 경우,  noContent로 반환 */
    @Operation(summary = "마이 그룹 조회")
    @GetMapping("/api/v1/groups/my-group")
    suspend fun getMyGroup(
        user: AuthUser
    ): ResponseEntity<Response<MyGroupResponse?>> {
        return myGroupFacade.getMyGroup(user).wrapOk()
    }
}
