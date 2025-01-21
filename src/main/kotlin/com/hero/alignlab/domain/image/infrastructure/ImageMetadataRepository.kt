package com.hero.alignlab.domain.image.infrastructure

import com.hero.alignlab.domain.image.domain.ImageMetadata
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Repository
interface ImageMetadataRepository : JpaRepository<ImageMetadata, Long> {
    fun countByCreatedAtBetween(startAt: LocalDateTime, endAt: LocalDateTime): Long

    fun deleteAllByUid(uid: Long)

    fun deleteAllByUidNotIn(uids: List<Long>)
}
