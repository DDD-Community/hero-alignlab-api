package com.hero.alignlab.domain.image.application

import com.hero.alignlab.client.s3.client.S3Client
import com.hero.alignlab.common.extension.executes
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.image.domain.ImageMetadata
import com.hero.alignlab.domain.image.domain.ImageType
import com.hero.alignlab.domain.image.infrastructure.ImageMetadataRepository
import com.hero.alignlab.domain.image.model.response.ImageResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service

@Service
class ImageService(
    private val imageMetadataRepository: ImageMetadataRepository,
    private val s3Client: S3Client,
    private val txTemplates: TransactionTemplates,
) {
    private val logger = KotlinLogging.logger {}

    suspend fun uploadImage(user: AuthUser, type: ImageType, image: FilePart): ImageResponse {
        val imageMetadata = uploadImage(user.uid, type, image)
        return ImageResponse.of(imageMetadata)
    }

    suspend fun uploadImage(uid: Long, type: ImageType, image: FilePart): ImageMetadata {
        val imageUrl = s3Client.upload(image)

        return txTemplates.writer.executes {
            imageMetadataRepository.save(
                ImageMetadata(
                    filename = image.filename(),
                    imageUrl = imageUrl,
                    type = type,
                    uid = uid
                )
            )
        }
    }
}
