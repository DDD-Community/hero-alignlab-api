package com.hero.alignlab.domain.auth.resolver

import com.hero.alignlab.domain.auth.application.AuthFacade
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.auth.model.AuthUserToken
import com.hero.alignlab.domain.auth.model.AuthUserToken.Companion.resolve
import org.springframework.core.MethodParameter
import org.springframework.core.ReactiveAdapterRegistry
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolverSupport
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

class ReactiveUserResolver(
    adapterRegistry: ReactiveAdapterRegistry,
    private val authFacade: AuthFacade,
) : HandlerMethodArgumentResolverSupport(adapterRegistry) {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == AuthUser::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange,
    ): Mono<Any> {
        val tokenMono = resolveToken(exchange.request)

        return authFacade.resolveAuthUser(tokenMono)
    }

    private fun resolveToken(request: ServerHttpRequest): Mono<AuthUserToken> {
        return request.headers
            .resolve()
            .toMono()
    }
}
