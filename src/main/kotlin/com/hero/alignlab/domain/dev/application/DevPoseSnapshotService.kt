package com.hero.alignlab.domain.dev.application

import com.hero.alignlab.common.extension.executes
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.dev.model.request.DevPoseSnapshotRequest
import com.hero.alignlab.domain.pose.application.PoseSnapshotService
import com.hero.alignlab.domain.pose.domain.PoseSnapshot
import com.hero.alignlab.domain.pose.domain.vo.PoseType
import com.hero.alignlab.domain.pose.model.PoseSnapshotModel
import com.hero.alignlab.domain.pose.model.request.PoseSnapshotRequest
import com.hero.alignlab.event.model.LoadPoseSnapshot
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.temporal.ChronoUnit

@Service
class DevPoseSnapshotService(
    private val poseSnapshotService: PoseSnapshotService,
    private val txTemplates: TransactionTemplates,
    private val publisher: ApplicationEventPublisher,
) {
    suspend fun create(request: DevPoseSnapshotRequest) {
        val daysBetween = ChronoUnit.DAYS.between(request.fromDate, request.toDate).toInt()
        val dailySnapshots = request.dailyCount

        for (day in 0..daysBetween) {
            val currentDate = request.fromDate.plusDays(day.toLong())
            for (count in 1..dailySnapshots) {
                val poseSnapshotRequest = generatePoseSnapshotRequest(request.uid)

                txTemplates.writer.executes {
                    val createdPoseSnapshot = poseSnapshotService.saveSync(
                        PoseSnapshot(
                            uid = request.uid,
                            score = poseSnapshotRequest.snapshot.score,
                            type = poseSnapshotRequest.type,
                            imageUrl = poseSnapshotRequest.imageUrl,
                        )
                    )

                    LoadPoseSnapshot(createdPoseSnapshot, poseSnapshotRequest.snapshot.keypoints)
                        .run { publisher.publishEvent(this) }
                }
            }
        }
    }

    private fun generatePoseSnapshotRequest(uid: Long): PoseSnapshotRequest {
        return PoseSnapshotRequest(
            snapshot = PoseSnapshotModel(
                keypoints = listOf(
                    PoseSnapshotModel.KeyPoint(
                        x = BigDecimal("340.15104727778066"),
                        y = BigDecimal("317.0014378682798"),
                        name = PoseSnapshotModel.PosePosition.nose,
                        confidence = BigDecimal("0.4566487967967987")
                    ),
                    PoseSnapshotModel.KeyPoint(
                        x = BigDecimal("377.45328131535103"),
                        y = BigDecimal("255.51991796000226"),
                        name = PoseSnapshotModel.PosePosition.left_eye,
                        confidence = BigDecimal("0.7127981781959534")
                    ),
                    PoseSnapshotModel.KeyPoint(
                        x = BigDecimal("296.70069464439877"),
                        y = BigDecimal("255.8227834988443"),
                        name = PoseSnapshotModel.PosePosition.right_eye,
                        confidence = BigDecimal("0.7658332586288452")
                    ),
                    PoseSnapshotModel.KeyPoint(
                        x = BigDecimal("409.9420757516539"),
                        y = BigDecimal("283.3670408973556"),
                        name = PoseSnapshotModel.PosePosition.left_ear,
                        confidence = BigDecimal("0.6566842794418335")
                    ),
                    PoseSnapshotModel.KeyPoint(
                        x = BigDecimal("243.45096777595006"),
                        y = BigDecimal("293.0522368514335"),
                        name = PoseSnapshotModel.PosePosition.right_ear,
                        confidence = BigDecimal("0.6061005592346191")
                    ),
                    PoseSnapshotModel.KeyPoint(
                        x = BigDecimal("474.296262666009"),
                        y = BigDecimal("461.63234326659443"),
                        name = PoseSnapshotModel.PosePosition.left_shoulder,
                        confidence = BigDecimal("0.3419668972492218")
                    ),
                    PoseSnapshotModel.KeyPoint(
                        x = BigDecimal("212.29553071932938"),
                        y = BigDecimal("405.9508591849053"),
                        name = PoseSnapshotModel.PosePosition.right_shoulder,
                        confidence = BigDecimal("0.1849706918001175")
                    ),
                    PoseSnapshotModel.KeyPoint(
                        x = BigDecimal("536.7644776153909"),
                        y = BigDecimal("472.4922462422612"),
                        name = PoseSnapshotModel.PosePosition.left_elbow,
                        confidence = BigDecimal("0.13967803120613098")
                    ),
                    PoseSnapshotModel.KeyPoint(
                        x = BigDecimal("115.94839192631409"),
                        y = BigDecimal("479.2769055909309"),
                        name = PoseSnapshotModel.PosePosition.right_elbow,
                        confidence = BigDecimal("0.14616511762142181")
                    ),
                    PoseSnapshotModel.KeyPoint(
                        x = BigDecimal("477.22051860157825"),
                        y = BigDecimal("463.2419869517527"),
                        name = PoseSnapshotModel.PosePosition.left_wrist,
                        confidence = BigDecimal("0.11746863275766373")
                    ),
                    PoseSnapshotModel.KeyPoint(
                        x = BigDecimal("115.32584645311717"),
                        y = BigDecimal("475.06100195679534"),
                        name = PoseSnapshotModel.PosePosition.right_wrist,
                        confidence = BigDecimal("0.06731665879487991")
                    ),
                    PoseSnapshotModel.KeyPoint(
                        x = BigDecimal("434.72166853076027"),
                        y = BigDecimal("481.99154533645805"),
                        name = PoseSnapshotModel.PosePosition.left_hip,
                        confidence = BigDecimal("0.05242015793919563")
                    ),
                    PoseSnapshotModel.KeyPoint(
                        x = BigDecimal("206.51050888234235"),
                        y = BigDecimal("487.54411662426895"),
                        name = PoseSnapshotModel.PosePosition.right_hip,
                        confidence = BigDecimal("0.04528944566845894")
                    ),
                    PoseSnapshotModel.KeyPoint(
                        x = BigDecimal("633.1560611828228"),
                        y = BigDecimal("357.2434379965817"),
                        name = PoseSnapshotModel.PosePosition.left_knee,
                        confidence = BigDecimal("0.09410975873470306")
                    ),
                    PoseSnapshotModel.KeyPoint(
                        x = BigDecimal("245.58417560462436"),
                        y = BigDecimal("473.55431375372086"),
                        name = PoseSnapshotModel.PosePosition.right_knee,
                        confidence = BigDecimal("0.22151082754135132")
                    ),
                    PoseSnapshotModel.KeyPoint(
                        x = BigDecimal("512.0801633054434"),
                        y = BigDecimal("321.2637690639215"),
                        name = PoseSnapshotModel.PosePosition.left_ankle,
                        confidence = BigDecimal("0.032857201993465424")
                    ),
                    PoseSnapshotModel.KeyPoint(
                        x = BigDecimal("265.4604333833633"),
                        y = BigDecimal("355.20512497446424"),
                        name = PoseSnapshotModel.PosePosition.right_ankle,
                        confidence = BigDecimal("0.05365283787250519")
                    )
                ),
                score = BigDecimal("0.5373632567269462")
            ),
            type = PoseType.CHIN_UTP,
            imageUrl = null
        )
    }
}
