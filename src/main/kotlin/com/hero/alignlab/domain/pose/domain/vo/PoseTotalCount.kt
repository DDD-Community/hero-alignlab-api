package com.hero.alignlab.domain.pose.domain.vo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.Serializable

/** 집계 데이터를 관리 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class PoseTotalCount(
    val count: MutableMap<PoseType, Int> = mutableMapOf(),
) : Serializable
