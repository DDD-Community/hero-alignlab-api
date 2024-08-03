package com.hero.alignlab.domain.dev.resource

import com.hero.alignlab.config.dev.DevResourceCheckConfig.Companion.devResource
import com.hero.alignlab.config.swagger.SwaggerTag.DEV_TAG
import com.hero.alignlab.domain.dev.application.DevWebsocketService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = DEV_TAG)
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class DevWebsocketResource(
    private val devWebsocketService: DevWebsocketService
) {
    @Operation(summary = "[DEV] websocket connection closed")
    @PostMapping("/api/dev/v1/websocket/connection-closed")
    suspend fun closedConnection(
        @RequestHeader("X-HERO-DEV-TOKEN") token: String
    ) = devResource(token) {
        devWebsocketService.forceCloseAllWebSocketSessions()
    }
}
