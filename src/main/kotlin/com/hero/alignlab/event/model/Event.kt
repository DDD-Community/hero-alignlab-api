package com.hero.alignlab.event.model

import com.hero.alignlab.common.extension.mapper
import com.hero.alignlab.common.extension.remoteIp
import com.hero.alignlab.domain.group.domain.Group
import com.hero.alignlab.domain.pose.domain.PoseSnapshot
import com.hero.alignlab.domain.pose.model.PoseSnapshotModel.KeyPoint
import org.springframework.web.server.ServerWebExchange
import java.time.LocalDateTime

sealed interface Event

open class BaseEvent(
    val publishAt: LocalDateTime = LocalDateTime.now(),
) : Event

data class CreateGroupEvent(
    val group: Group
) : BaseEvent()

data class LoadPoseSnapshot(
    val poseSnapshot: PoseSnapshot,
    val keyPoints: List<KeyPoint>,
) : BaseEvent()

data class SystemActionLogEvent(
    val ipAddress: String?,
    val method: String?,
    val path: String?,
    val userAgent: String?,
    val host: String?,
    val referer: String?,
    val extra: String?,
) : BaseEvent() {
    companion object {
        private const val USER_AGENT = "User-Agent"
        private const val HOST = "Host"
        private const val REFERER = "Referer"

        fun from(exchange: ServerWebExchange): SystemActionLogEvent {
            val request = exchange.request

            return SystemActionLogEvent(
                ipAddress = request.remoteIp,
                method = request.method.name(),
                path = request.uri.path,
                userAgent = request.headers[USER_AGENT].toString(),
                host = request.headers[HOST].toString(),
                referer = request.headers[REFERER].toString(),
                extra = mapper.writeValueAsString(request.headers)
            )
        }
    }
}

data class WithdrawEvent(
    val uid: Long,
) : BaseEvent()
