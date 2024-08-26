package com.hero.alignlab.domain.pose.domain.vo

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue

enum class PoseType(val nameKor: String) {
    /** 좋은 포즈 */
    GOOD("좋은 상태"),

    /** 나쁜 포즈 */
    TURTLE_NECK("거북목"),
    SHOULDER_TWIST("어깨 틀어짐"),
    CHIN_UTP("턱 괴기"),
    TAILBONE_SIT("꼬리뼈 앉기"),

    /** 예외처리 */
    @JsonEnumDefaultValue
    UNKNOWN("예외 타입"),
    ;

    companion object {
        val BAD_POSE = setOf(TURTLE_NECK, SHOULDER_TWIST, CHIN_UTP, TAILBONE_SIT)
    }
}
