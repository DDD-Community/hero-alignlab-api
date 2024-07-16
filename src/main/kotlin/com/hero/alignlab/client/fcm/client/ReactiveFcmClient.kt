package com.hero.alignlab.client.fcm.client

import com.hero.alignlab.client.fcm.config.FcmProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.reactive.function.client.WebClient

class ReactiveFcmClient(
    fcmProperties: FcmProperties,
    webClient: WebClient,
) : FcmClient {
    private val logger = KotlinLogging.logger {}

}
