package com.hero.alignlab.client.s3.client

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.hero.alignlab.client.s3.config.S3Properties
import com.hero.alignlab.common.extension.encodeURL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.withContext
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class S3Client(
    private val s3Properties: S3Properties,
    private val amazonS3: AmazonS3
) {
    suspend fun upload(filePart: FilePart): String {
        return withContext(Dispatchers.IO) {
            filePart.content()
                .reduce { buffer1, buffer2 -> buffer1.write(buffer2) }
                .flatMap { dataBuffer ->
                    runCatching {
                        resolveRequest(filePart, dataBuffer)
                            .run { amazonS3.putObject(this) }
                        imageUrl(filePart.filename())
                    }.fold(
                        onSuccess = { url ->
                            Mono.just(url)
                        },
                        onFailure = { error ->
                            Mono.error(error)
                        }
                    ).also {
                        /** Release the data buffer */
                        DataBufferUtils.release(dataBuffer)
                    }
                }.awaitSingle()
        }
    }

    private fun resolveRequest(filePart: FilePart, dataBuffer: DataBuffer): PutObjectRequest? {
        val metadata = resolveObjectMetadata(filePart, dataBuffer)

        val inputStream = dataBuffer.asInputStream()

        return PutObjectRequest(imageBucketUrl(), filePart.filename(), inputStream, metadata)
            .withCannedAcl(CannedAccessControlList.PublicRead)
    }

    private fun resolveObjectMetadata(
        filePart: FilePart,
        dataBuffer: DataBuffer
    ): ObjectMetadata {
        return ObjectMetadata().apply {
            contentType = filePart.headers().contentType.toString()
            contentLength = dataBuffer.readableByteCount().toLong()
        }
    }

    fun imageBucketUrl() = "${s3Properties.bucket}/images"

    fun imageUrl(filename: String): String {
        return "${s3Properties.bucketUrl}/images/${filename.encodeURL()}"
    }
}
