package com.hero.alignlab.client.s3.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationPropertiesBinding
class S3Config {
    private val logger = KotlinLogging.logger { }

    @Bean
    fun amazonS3Client(s3Properties: S3Properties): AmazonS3 {
        logger.info { "s3Properties Binding $s3Properties" }

        val credentials = BasicAWSCredentials(s3Properties.accessKey, s3Properties.secretKey)

        return AmazonS3ClientBuilder
            .standard()
            .withCredentials(AWSStaticCredentialsProvider(credentials))
            .withRegion(s3Properties.region)
            .build()
    }
}
