package com.hero.alignlab.domain.image.resource

import com.hero.alignlab.common.extension.wrapCreated
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.image.application.ImageService
import com.hero.alignlab.domain.image.domain.ImageType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*

@Tag(name = "Image API")
@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class ImageResource(
    private val imageService: ImageService
) {
    /** 이미지 업로드시 type을 꼭 명시해야 한다. */
    @Operation(summary = "이미지 단건 업로드")
    @PostMapping(path = ["/api/v1/images"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    suspend fun uploadImage(
        user: AuthUser,
        @RequestParam type: ImageType,
        @RequestPart("image") image: FilePart,
    ) = imageService.uploadImage(
        user = user,
        type = type,
        image = image
    ).wrapCreated()

    /** 이미지 업로드시 type을 꼭 명시해야 한다. */
    @Operation(summary = "이미지 벌크 업로드")
    @PostMapping(path = ["/api/v1/images/bulk"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    suspend fun uploadImage(
        user: AuthUser,
        @RequestParam type: ImageType,
        @RequestPart("images") images: List<FilePart>,
    ) = imageService.bulkUploadImage(
        user = user,
        type = type,
        images = images
    ).wrapCreated()
}
