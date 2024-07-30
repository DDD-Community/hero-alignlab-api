package com.hero.alignlab.client.s3.config

import jakarta.validation.constraints.NotBlank
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

@Configuration
@ConfigurationPropertiesBinding
@Validated
class S3Properties {
    @field:NotBlank
    @Value("\${cloud.aws.credentials.access-key}")
    var accessKey: String? = null

    @field:NotBlank
    @Value("\${cloud.aws.credentials.secret-key}")
    var secretKey: String? = null

    @field:NotBlank
    @Value("\${cloud.aws.region.static}")
    var region: String? = null

    @field:NotBlank
    @Value("\${cloud.aws.s3.bucket}")
    var bucket: String? = null

    @field:NotBlank
    @Value("\${cloud.aws.s3.bucketUrl}")
    var bucketUrl: String? = null
}

