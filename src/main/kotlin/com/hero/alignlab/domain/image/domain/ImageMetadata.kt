package com.hero.alignlab.domain.image.domain

import com.hero.alignlab.domain.common.domain.BaseEntity
import com.hero.alignlab.domain.image.domain.vo.ImageType
import jakarta.persistence.*

@Entity
@Table(name = "image_metadata")
class ImageMetadata(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "uid")
    val uid: Long = 0L,

    @Column(name = "filename")
    val filename: String,

    /** cdn image url을 저장 */
    @Column(name = "image_url")
    val imageUrl: String,

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    val type: ImageType
) : BaseEntity()
