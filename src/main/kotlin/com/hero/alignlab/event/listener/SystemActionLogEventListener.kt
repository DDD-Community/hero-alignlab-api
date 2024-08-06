package com.hero.alignlab.event.listener

import com.hero.alignlab.domain.log.application.SystemActionLogService
import com.hero.alignlab.domain.log.domain.SystemActionLog
import com.hero.alignlab.event.model.SystemActionLogEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class SystemActionLogEventListener(
    private val systemActionLogService: SystemActionLogService,
) {
    @EventListener
    fun subscribe(event: SystemActionLogEvent) {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            if (filterLog(event)) {
                SystemActionLog(
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
        return !event.path.equals("/api/v1/health")
    }
}
