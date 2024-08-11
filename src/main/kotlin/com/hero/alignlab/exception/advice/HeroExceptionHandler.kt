package com.hero.alignlab.exception.advice

import com.hero.alignlab.common.model.ErrorResponse
import com.hero.alignlab.exception.HeroException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class HeroExceptionHandler {
    private val logger = KotlinLogging.logger { }

    @ExceptionHandler(HeroException::class)
    protected fun handleHeroException(e: HeroException): ResponseEntity<ErrorResponse> {
        logger.warn { "HeroException ${e.message}" }
        val response = ErrorResponse(
            errorCode = e.errorCode.name,
            reason = e.message ?: e.errorCode.description,
            extra = e.extra
        )
        return ResponseEntity(response, e.errorCode.status)
    }
}
