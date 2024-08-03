package com.hero.alignlab.domain.pose.model

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.math.BigDecimal

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PoseSnapshotModel(
    val keypoints: List<KeyPoint>,
    val score: BigDecimal,
) {
    data class KeyPoint(
        val y: BigDecimal,
        val x: BigDecimal,
        val name: PosePosition,
        val confidence: BigDecimal
    )

    enum class PosePosition {
        nose,
        left_eye,
        right_eye,
        left_ear,
        right_ear,
        left_shoulder,
        right_shoulder,
        left_elbow,
        right_elbow,
        left_wrist,
        right_wrist,
        left_hip,
        right_hip,
        left_knee,
        right_knee,
        left_ankle,
        right_ankle,
        ;

        fun toPosition(): com.hero.alignlab.domain.pose.domain.vo.PosePosition {
            return when (this) {
                nose -> com.hero.alignlab.domain.pose.domain.vo.PosePosition.NOSE
                left_eye -> com.hero.alignlab.domain.pose.domain.vo.PosePosition.LEFT_EYE
                right_eye -> com.hero.alignlab.domain.pose.domain.vo.PosePosition.RIGHT_EYE
                left_ear -> com.hero.alignlab.domain.pose.domain.vo.PosePosition.LEFT_EAR
                right_ear -> com.hero.alignlab.domain.pose.domain.vo.PosePosition.RIGHT_EAR
                left_shoulder -> com.hero.alignlab.domain.pose.domain.vo.PosePosition.LEFT_SHOULDER
                right_shoulder -> com.hero.alignlab.domain.pose.domain.vo.PosePosition.RIGHT_SHOULDER
                left_elbow -> com.hero.alignlab.domain.pose.domain.vo.PosePosition.LEFT_ELBOW
                right_elbow -> com.hero.alignlab.domain.pose.domain.vo.PosePosition.RIGHT_ELBOW
                left_wrist -> com.hero.alignlab.domain.pose.domain.vo.PosePosition.LEFT_WRIST
                right_wrist -> com.hero.alignlab.domain.pose.domain.vo.PosePosition.RIGHT_WRIST
                left_hip -> com.hero.alignlab.domain.pose.domain.vo.PosePosition.LEFT_HIP
                right_hip -> com.hero.alignlab.domain.pose.domain.vo.PosePosition.RIGHT_HIP
                left_knee -> com.hero.alignlab.domain.pose.domain.vo.PosePosition.LEFT_KNEE
                right_knee -> com.hero.alignlab.domain.pose.domain.vo.PosePosition.RIGHT_KNEE
                left_ankle -> com.hero.alignlab.domain.pose.domain.vo.PosePosition.LEFT_ANKLE
                right_ankle -> com.hero.alignlab.domain.pose.domain.vo.PosePosition.RIGHT_ANKLE
            }
        }
    }
}
