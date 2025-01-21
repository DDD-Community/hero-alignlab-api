package com.hero.alignlab.domain.dev.resource

import com.hero.alignlab.config.swagger.SwaggerTag.DEV_TAG
import com.hero.alignlab.domain.auth.model.DevAuthUser
import com.hero.alignlab.domain.dev.application.DevCheerUpService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = DEV_TAG)
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class DevCheerUpResource(
    private val devCheerUpService: DevCheerUpService,
) {
    @Operation(summary = "cheer-up delete all")
    @DeleteMapping("/api/dev/v1/cheer-ups")
    suspend fun deleteAll(dev: DevAuthUser) {
        devCheerUpService.deleteAll()
    }
}
