package com.hero.alignlab.config.rsocket

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.codec.cbor.Jackson2CborDecoder
import org.springframework.http.codec.cbor.Jackson2CborEncoder
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import reactor.util.retry.Retry
import java.net.URI
import java.time.Duration

@Configuration
@EnableConfigurationProperties(RsocketConfig.RsocketCustomProperties::class)
class RsocketConfig {
    private val logger = KotlinLogging.logger {}

    @Bean
    fun rsocketRequester(
        rsocketStrategies: RSocketStrategies,
        rsocketCustomProperties: RsocketCustomProperties
    ): RSocketRequester {
        return RSocketRequester.builder()
            .rsocketStrategies(rsocketStrategies)
            .rsocketConnector { connector ->
                connector.reconnect(
                    Retry
                        .fixedDelay(Long.MAX_VALUE, Duration.ofSeconds(3))
                        .doAfterRetry { logger.info { "Retry connection to Rsocket Connection.." } }
                )
            }
            .dataMimeType(MediaType.APPLICATION_JSON)
            .websocket(URI.create(rsocketCustomProperties.url))
    }

    @Bean
    fun rsocketStrategies(): RSocketStrategies {
        return RSocketStrategies.builder()
            .encoders { it.add(Jackson2CborEncoder()) }
            .decoders { it.add(Jackson2CborDecoder()) }
            .build()
    }

    @ConfigurationProperties(prefix = "rsocket")
    data class RsocketCustomProperties(
        val url: String = "",
    )
}
