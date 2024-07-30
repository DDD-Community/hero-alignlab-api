package com.hero.alignlab.domain.image.model.response

import com.hero.alignlab.domain.image.domain.ImageMetadata
import java.time.LocalDateTime

data class ImageResponse(
    val id: Long,
    val filename: String,
    val imageUrl: String,
    val createdBy: Long,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun of(image: ImageMetadata): ImageResponse {
            return ImageResponse(
                id = image.id,
                filename = image.filename,
                imageUrl = image.imageUrl,
                createdBy = image.uid,
                createdAt = image.createdAt
            )
        }
    }
}
