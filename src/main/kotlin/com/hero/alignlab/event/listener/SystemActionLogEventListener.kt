package com.hero.alignlab.event.listener

import com.hero.alignlab.domain.auth.application.AuthFacade
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.auth.model.AuthUserToken
import com.hero.alignlab.domain.log.application.SystemActionLogService
import com.hero.alignlab.domain.log.domain.SystemActionLog
import com.hero.alignlab.event.model.SystemActionLogEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toMono

@Component
class SystemActionLogEventListener(
    private val systemActionLogService: SystemActionLogService,
    private val authFacade: AuthFacade,
) {
    @EventListener
    fun subscribe(event: SystemActionLogEvent) {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            val authUser = event.token
                ?.let { token -> AuthUserToken.from(token).toMono() }
                ?.let { token ->
                    runCatching {
                        authFacade.resolveAuthUser(token).awaitSingleOrNull() as? AuthUser
                    }.getOrNull()
                }

            if (filterLog(event)) {
                SystemActionLog(
                    uid = authUser?.uid,
                    ipAddress = event.ipAddress,
                    path = event.path,
                    httpMethod = event.method,
                    userAgent = event.userAgent,
                    host = event.host,
                    referer = event.referer,
                    extra = event.extra
                ).run { systemActionLogService.record(this) }
            }
        }
    }

    /** 불필요한 로그는 적재하지 않는다. */
    private fun filterLog(event: SystemActionLogEvent): Boolean {
        if (event.path.equals("/api/v1/health")) {
            return false
        }

        if (event.path?.startsWith("/api/") == true) {
            return true
        }

        return false
    }
}
