package com.hero.alignlab.domain.auth.resolver

import com.hero.alignlab.config.dev.DevResourceCheckConfig
import com.hero.alignlab.domain.auth.model.DevAuthToken
import com.hero.alignlab.domain.auth.model.DevAuthToken.Companion.resolveDevToken
import com.hero.alignlab.domain.auth.model.DevAuthUser
import org.springframework.core.MethodParameter
import org.springframework.core.ReactiveAdapterRegistry
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolverSupport
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

class ReactiveDevResolver(
    adapterRegistry: ReactiveAdapterRegistry,
    private val devResourceCheckConfig: DevResourceCheckConfig,
) : HandlerMethodArgumentResolverSupport(adapterRegistry) {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == DevAuthUser::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange,
    ): Mono<Any> {
        val tokenMono = resolveToken(exchange.request)

        return devResourceCheckConfig.checkDev(tokenMono)
    }

    private fun resolveToken(request: ServerHttpRequest): Mono<DevAuthToken> {
        return request.headers
            .resolveDevToken()
            .toMono()
    }
}
