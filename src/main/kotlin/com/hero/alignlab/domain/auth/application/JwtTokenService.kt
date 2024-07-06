package com.hero.alignlab.domain.auth.application

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.module.kotlin.readValue
import com.hero.alignlab.config.JwtConfig
import com.hero.alignlab.domain.auth.model.AuthUserToken
import com.hero.alignlab.domain.auth.model.AuthUserTokenPayload
import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.InvalidTokenException
import com.hero.alignlab.extension.decodeBase64
import com.hero.alignlab.extension.mapper
import com.hero.alignlab.extension.toInstant
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

private const val ACCESS_TOKEN = "accessToken"

@Service
class JwtTokenService(
    private val jwtConfig: JwtConfig,
) {
    private val logger = KotlinLogging.logger {}

    private val accessJwtVerifier = JWT
        .require(Algorithm.HMAC256(jwtConfig.secret))
        .withIssuer(jwtConfig.issuer)
        .withAudience(jwtConfig.audience)
        .withClaim("type", ACCESS_TOKEN)
        .build()

    private val accessJwtVerifierWithExtendedExpiredAt = JWT
        .require(Algorithm.HMAC256(jwtConfig.secret))
        .withIssuer(jwtConfig.issuer)
        .withAudience(jwtConfig.audience)
        .withClaim("type", ACCESS_TOKEN)
        .acceptExpiresAt(jwtConfig.refreshExp.toLong())
        .build()

    fun createToken(id: Long, tokenExpiredAt: LocalDateTime): String {
        return JWT.create().apply {
            this.withIssuer(jwtConfig.issuer)
            this.withAudience(jwtConfig.audience)
            this.withClaim("id", id)
            this.withClaim("type", ACCESS_TOKEN)
            this.withExpiresAt(Date.from(tokenExpiredAt.toInstant()))
        }.sign(Algorithm.HMAC256(jwtConfig.secret))
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
