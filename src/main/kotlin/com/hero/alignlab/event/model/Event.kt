package com.hero.alignlab.event.model

import com.hero.alignlab.domain.group.domain.Group
import com.hero.alignlab.domain.pose.domain.PoseSnapshot
import com.hero.alignlab.domain.pose.model.PoseSnapshotModel.KeyPoint
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
