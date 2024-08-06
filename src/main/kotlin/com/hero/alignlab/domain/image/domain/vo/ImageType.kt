package com.hero.alignlab.domain.image.domain.vo

/**
 * Image Upload Type
 * - DOMAIN_SERVICE
 * - type명은 도메인과 서비스명을 기반으로 만든다
 **/
enum class ImageType {
    /** user */
    USER_PROFILE,

    /** pose snapshot */
    POSE_SNAPSHOT,

    /** misc */
    MISC,
    ;
}
