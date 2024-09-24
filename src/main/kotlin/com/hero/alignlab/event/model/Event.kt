package com.hero.alignlab.event.model

import com.hero.alignlab.common.extension.mapper
import com.hero.alignlab.common.extension.remoteIp
import com.hero.alignlab.domain.auth.model.AUTH_TOKEN_KEY
import com.hero.alignlab.domain.discussion.domain.Discussion
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
    val token: String?,
    val extra: String?,
) : BaseEvent() {
    companion object {
        private const val USER_AGENT = "USER-AGENT"
        private const val HOST = "HOST"
        private const val REFERER = "REFERER"

        fun from(exchange: ServerWebExchange): SystemActionLogEvent {
            val request = exchange.request

            val headers = exchange.request.headers.toSingleValueMap()
                .mapKeys { header -> header.key.uppercase() }

            return SystemActionLogEvent(
                ipAddress = request.remoteIp,
                method = request.method.name(),
                path = request.uri.path,
                userAgent = headers[USER_AGENT],
                host = headers[HOST],
                referer = headers[REFERER],
                token = headers[AUTH_TOKEN_KEY],
                extra = mapper.writeValueAsString(request.headers)
            )
        }
    }
}

data class WithdrawEvent(
    val uid: Long,
) : BaseEvent()

data class DiscussionEvent(
    val discussion: Discussion,
) : BaseEvent()
