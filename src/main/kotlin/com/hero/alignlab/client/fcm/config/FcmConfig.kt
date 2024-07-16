package com.hero.alignlab.client.fcm.config

import com.hero.alignlab.client.WebClientFactory
import com.hero.alignlab.client.fcm.client.FcmClient
import com.hero.alignlab.client.fcm.client.ReactiveFcmClient
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(FcmProperties::class)
class FcmConfig {
    @Bean
    fun fcmClient(fcmProperties: FcmProperties): FcmClient {
        return ReactiveFcmClient(
            fcmProperties = fcmProperties,
            // 향후, 변경 작업 진행 필요.
            webClient = WebClientFactory.generate(baseUrl = "http://localhost:8080")
        )
    }
}

@ConfigurationProperties(prefix = "push.fcm")
data class FcmProperties(
    val type: String,
    val projectId: String,
    val privateKeyId: String,
    val privateKey: String,
    val clientEmail: String,
    val clientId: String,
    val authUri: String,
    val tokenUri: String,
    val authProviderX509CertUrl: String,
    val clientX509CertUrl: String,
    val universeDomain: String
)
