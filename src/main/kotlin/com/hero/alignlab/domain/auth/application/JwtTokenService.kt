package com.hero.alignlab.domain.auth.application

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.module.kotlin.readValue
import com.hero.alignlab.common.extension.decodeBase64
import com.hero.alignlab.common.extension.mapper
import com.hero.alignlab.common.extension.toInstant
import com.hero.alignlab.config.jwt.JwtConfig
import com.hero.alignlab.domain.auth.model.AuthUserToken
import com.hero.alignlab.domain.auth.model.AuthUserTokenPayload
import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.InvalidTokenException
import io.github.oshai.kotlinlogging.KotlinLogging
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

private const val ACCESS_TOKEN = "accessToken"

class JwtTokenService(
    private val jwtProperties: JwtConfig.JwtProperties,
) {
    private val logger = KotlinLogging.logger {}

    private val accessJwtVerifier = JWT
        .require(Algorithm.HMAC256(jwtProperties.secret))
        .withIssuer(jwtProperties.issuer)
        .withAudience(jwtProperties.audience)
        .withClaim("type", ACCESS_TOKEN)
        .build()

    private val accessJwtVerifierWithExtendedExpiredAt = JWT
        .require(Algorithm.HMAC256(jwtProperties.secret))
        .withIssuer(jwtProperties.issuer)
        .withAudience(jwtProperties.audience)
        .withClaim("type", ACCESS_TOKEN)
        .acceptExpiresAt(jwtProperties.refreshExp.toLong())
        .build()

    fun createToken(id: Long, tokenExpiredAt: LocalDateTime): String {
        return JWT.create().apply {
            this.withIssuer(jwtProperties.issuer)
            this.withAudience(jwtProperties.audience)
            this.withClaim("id", id)
            this.withClaim("type", ACCESS_TOKEN)
            this.withExpiresAt(Date.from(tokenExpiredAt.toInstant()))
        }.sign(Algorithm.HMAC256(jwtProperties.secret))
    }

    fun verifyToken(token: AuthUserToken): AuthUserTokenPayload {
        val payload = accessJwtVerifier.verify(token.value)
            .payload
            .decodeBase64()

        return mapper.readValue(payload)
    }

    fun verifyTokenWithExtendedExpiredAt(token: String): AuthUserTokenPayload {
        val payload = runCatching { accessJwtVerifierWithExtendedExpiredAt.verify(token).payload.decodeBase64() }
            .getOrNull() ?: throw InvalidTokenException(ErrorCode.INVALID_TOKEN)

        return mapper.readValue(payload)
    }

    fun verifyTokenMono(authUserToken: Mono<AuthUserToken>): Mono<AuthUserTokenPayload> {
        return authUserToken.flatMap { jwtToken ->
            Mono.fromCallable { verifyToken(jwtToken) }
                .onErrorResume { e ->
                    logger.warn { e.message }
                    Mono.error(InvalidTokenException(ErrorCode.FAIL_TO_VERIFY_TOKEN_ERROR))
                }
        }
    }
}
