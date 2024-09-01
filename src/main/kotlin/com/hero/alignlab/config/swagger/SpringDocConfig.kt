package com.hero.alignlab.config.swagger

import com.hero.alignlab.domain.auth.model.AUTH_TOKEN_KEY
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.auth.model.DEV_AUTH_TOKEN_KEY
import com.hero.alignlab.domain.auth.model.DevAuthUser
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.utils.SpringDocUtils
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.result.view.RequestContext
import org.springframework.web.server.WebSession

/**
 * **Swagger**
 *
 * Provide detailed explanations based on comments.
 *
 * [Local Swagger UI](http://localhost:8080/webjars/swagger-ui/index.html)
 */
@Configuration
class SpringDocConfig(
    private val buildProperties: BuildProperties,
) {
    init {
        SpringDocUtils
            .getConfig()
            .addRequestWrapperToIgnore(
                WebSession::class.java,
                RequestContext::class.java,
                AuthUser::class.java,
                DevAuthUser::class.java,
            )
    }

    @Bean
    fun openApi(): OpenAPI {
        return OpenAPI()
            .components(authSetting())
            .addServersItem(Server().url("/"))
            .info(
                Info()
                    .title(buildProperties.name)
                    .version(buildProperties.version)
                    .description("Hero Alignlab Rest API Docs")
            )
            .addSecurityItem(SecurityRequirement().addList(AUTH_TOKEN_KEY))
            .addSecurityItem(SecurityRequirement().addList(DEV_AUTH_TOKEN_KEY))
    }

    private fun authSetting(): Components {
        return Components()
            .addSecuritySchemes(
                AUTH_TOKEN_KEY,
                SecurityScheme()
                    .name(AUTH_TOKEN_KEY)
                    .type(SecurityScheme.Type.APIKEY)
                    .`in`(SecurityScheme.In.HEADER)
            ).addSecuritySchemes(
                DEV_AUTH_TOKEN_KEY,
                SecurityScheme()
                    .name(DEV_AUTH_TOKEN_KEY)
                    .type(SecurityScheme.Type.APIKEY)
                    .`in`(SecurityScheme.In.HEADER)
            )
    }
}

object SwaggerTag {
    const val DEV_TAG = "Dev Resource, 테스트용 API"
}
