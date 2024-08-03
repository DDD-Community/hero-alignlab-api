package com.hero.alignlab.common.extension

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicLong

import kotlin.coroutines.cancellation.CancellationException

object CoroutineExtension {
    private val logger = KotlinLogging.logger {}

    suspend fun <T> retryOnError(
        times: Int = Int.MAX_VALUE,
        initialDelay: Long = 100,
        maxDelay: Long = 1000,
        factor: Double = 2.0,
        block: suspend () -> T,
    ): T {
        val currentDelay = AtomicLong(initialDelay)

        repeat(times - 1) {
            runCatching {
                return block()
            }.onFailure { e ->
                when (e) {
                    is CancellationException -> throw e
                    else -> logger.warn { "Error retry. message=${e.message}" }
                }
            }

            delay(currentDelay.get())

            currentDelay.set(
                (currentDelay.get() * factor).toLong().coerceAtMost(maxDelay)
            )
        }

        return block()
    }
}
