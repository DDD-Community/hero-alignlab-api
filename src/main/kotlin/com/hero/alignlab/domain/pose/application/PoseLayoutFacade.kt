package com.hero.alignlab.domain.pose.application

import com.hero.alignlab.common.extension.executes
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.pose.domain.PoseLayout
import com.hero.alignlab.domain.pose.domain.PoseLayoutPoint
import com.hero.alignlab.domain.pose.model.request.PoseLayoutRequest
import com.hero.alignlab.domain.pose.model.response.GetPoseLayoutResponse
import com.hero.alignlab.domain.pose.model.response.GetRecentPoseLayoutResponse
import com.hero.alignlab.domain.pose.model.response.PoseLayoutPointResponse
import com.hero.alignlab.domain.pose.model.response.RegisterPoseLayoutResponse
import org.springframework.stereotype.Component

@Component
class PoseLayoutFacade(
    private val txTemplates: TransactionTemplates,
    private val poseLayoutService: PoseLayoutService,
    private val postLayoutPointService: PoseLayoutPointService,
) {
    suspend fun getRecentPoseLayout(user: AuthUser): GetRecentPoseLayoutResponse {
        val poseLayout = poseLayoutService.findTop1ByUidOrderByIdDesc(user.uid)
            ?: return GetRecentPoseLayoutResponse()

        val points = postLayoutPointService.findAllByPoseLayoutId(poseLayout.id)
            .map { point -> PoseLayoutPointResponse.from(point) }

        return GetRecentPoseLayoutResponse(poseLayout.id, points)
    }

    suspend fun getPoseLayout(user: AuthUser, id: Long): GetPoseLayoutResponse {
        val poseLayout = poseLayoutService.findByIdAndUidOrThrow(id, user.uid)

        val points = postLayoutPointService.findAllByPoseLayoutId(poseLayout.id)
            .map { point -> PoseLayoutPointResponse.from(point) }

        return GetPoseLayoutResponse(poseLayout.id, points)
    }

    suspend fun register(user: AuthUser, request: PoseLayoutRequest): RegisterPoseLayoutResponse {
        val createdPoseLayout = txTemplates.writer.executes {
            val poseLayout = poseLayoutService.saveSync(PoseLayout(uid = user.uid))

            request.points.map { point ->
                PoseLayoutPoint(
                    poseLayoutId = poseLayout.id,
                    position = point.position,
                    x = point.x,
                    y = point.y,
                )
            }.run { postLayoutPointService.saveAllSync(this) }

            poseLayout
        }

        return RegisterPoseLayoutResponse(createdPoseLayout.id)
    }
}
