package com.hero.alignlab.domain.dev.resource

import com.hero.alignlab.config.swagger.SwaggerTag.DEV_TAG
import com.hero.alignlab.domain.auth.model.DevAuthUser
import com.hero.alignlab.domain.dev.application.DevWebsocketService
import com.hero.alignlab.ws.handler.ReactiveGroupUserWebSocketHandler
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = DEV_TAG)
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class DevWebsocketResource(
    private val devWebsocketService: DevWebsocketService,
    private val reactiveGroupUserWebSocketHandler: ReactiveGroupUserWebSocketHandler
) {
    @Operation(summary = "[DEV] websocket connection closed")
    @PostMapping("/api/dev/v1/websocket/connection-closed")
    suspend fun closedConnection(
        dev: DevAuthUser,
    ) {
        devWebsocketService.forceCloseAllWebSocketSessions()
    }

    @Operation(summary = "[DEV] 현재 참여자 정보 조회")
    @PostMapping("/api/dev/v1/websocket/group-users")
    suspend fun getGroupUsers(
        dev: DevAuthUser,
    ) = reactiveGroupUserWebSocketHandler.getWsGroupUsers()
}
