package com.hero.alignlab.domain.pose.domain

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue

enum class PoseType(val nameKor: String) {
    /** 좋은 포즈 */
    GOOD("좋은 상태"),

    /** 나쁜 포즈 */
    TURTLE_NECK("거북목"),
    SHOULDER_TWIST("어깨 틀어짐"),
    CHIN_UTP("턱 괴기"),

    /** 예외처리 */
    @JsonEnumDefaultValue
    UNKNOWN("예외 타입"),
    ;
}
