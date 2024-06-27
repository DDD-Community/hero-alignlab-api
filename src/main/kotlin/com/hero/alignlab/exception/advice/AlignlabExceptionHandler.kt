package com.hero.alignlab.exception.advice

import com.hero.alignlab.dto.ErrorResponse
import com.hero.alignlab.exception.AlignlabException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class AlignlabExceptionHandler {
    private val logger = KotlinLogging.logger { }

    @ExceptionHandler(AlignlabException::class)
    protected fun handleAlignlabException(e: AlignlabException): ResponseEntity<ErrorResponse> {
        logger.warn { "AlignlabException ${e.message}" }
        val response = ErrorResponse(
            errorCode = e.errorCode.name,
            reason = e.message ?: e.errorCode.description,
            extra = e.extra
        )
        return ResponseEntity(response, e.errorCode.status)
    }
}
