package com.hero.alignlab.extension

import com.hero.alignlab.dto.PageResponseDto
import org.springframework.data.domain.Page
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.net.URI

/** Wrap Response Page */
fun <T> Page<T>.wrapPage() = PageResponseDto(this)

/** Wrap Response Ok */
fun <T> T.wrapOk() = ResponseEntity.ok(this)

/** Wrap Response Created */
fun <T> T.wrapCreated() = ResponseEntity.status(HttpStatus.CREATED).body(this)

/** Wrap Response Void */
fun Unit.wrapVoid() = ResponseEntity.noContent().build<Unit>()

fun String.wrapRedirected(): ResponseEntity<Unit> {
    val headers = HttpHeaders()
    headers.location = URI.create(this)
    return ResponseEntity<Unit>(headers, HttpStatus.PERMANENT_REDIRECT)
}
