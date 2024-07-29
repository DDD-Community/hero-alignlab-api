package com.hero.alignlab.client.kakao

import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitBodyOrNull

abstract class SuspendableClient(
    protected val client: WebClient,
) {
    protected suspend inline fun <reified T : Any> WebClient.RequestHeadersSpec<*>.request(): T {
        return this.retrieve().awaitBody<T>()
    }

    protected suspend inline fun <reified T : Any> WebClient.RequestHeadersSpec<*>.requestOrNull(): T? {
        return this.retrieve().awaitBodyOrNull<T>()
    }
}
