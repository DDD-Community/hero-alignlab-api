package com.hero.alignlab.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(val status: HttpStatus, val description: String) {
    /** Common Error Code */
    BAD_REQUEST_ERROR(HttpStatus.BAD_REQUEST, "bad request"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류, 관리자에게 문의하세요"),
    INVALID_INPUT_VALUE_ERROR(HttpStatus.BAD_REQUEST, "input is invalid value"),
    INVALID_TYPE_VALUE_ERROR(HttpStatus.BAD_REQUEST, "invalid type value"),
    METHOD_NOT_ALLOWED_ERROR(HttpStatus.METHOD_NOT_ALLOWED, "Method type is invalid"),
    INVALID_MEDIA_TYPE_ERROR(HttpStatus.BAD_REQUEST, "invalid media type"),
    QUERY_DSL_NOT_EXISTS_ERROR(HttpStatus.NOT_FOUND, "not found query dsl"),
    COROUTINE_CANCELLATION_ERROR(HttpStatus.BAD_REQUEST, "coroutine cancellation error"),
    FAIL_TO_TRANSACTION_TEMPLATE_EXECUTE_ERROR(HttpStatus.BAD_REQUEST, "fail to tx-templates execute error"),

    /** redis pub-sub */
    NOT_FOUND_CHANNEL_ERROR(HttpStatus.NOT_FOUND, "channel not found"),
    NOT_FOUND_MESSAGE_ERROR(HttpStatus.NOT_FOUND, "message not found"),

    /** Auth Error Code */
    NOT_FOUND_TOKEN_ERROR(HttpStatus.UNAUTHORIZED, "not found token"),
    FAIL_TO_VERIFY_TOKEN_ERROR(HttpStatus.UNAUTHORIZED, "fail to verify token"),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효한 엑세스 토큰이 아닙니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "유효한 리프레시 토큰이 아닙니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "유효한 토큰이 아닙니다."),
    NO_AUTHORITY_ERROR(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    INVALID_OAUTH_PROVIDER(HttpStatus.BAD_REQUEST, "잘못된 oauth 벤더입니다."),
    DUPLICATED_USERNAME_ERROR(HttpStatus.BAD_REQUEST, "중복된 아이디 입니다."),

    /** OAuth Error Code */
    NOT_FOUND_OAUTH_PROVIDER_ERROR(HttpStatus.NOT_FOUND, "현재 미지원하는 제공자입니다."),

    /** User Error Code */
    NOT_FOUND_USER_ERROR(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."),

    /** Group Error Code */
    DUPLICATE_GROUP_NAME_ERROR(HttpStatus.BAD_REQUEST, "중복된 그룹명입니다."),
    NOT_FOUND_GROUP_ERROR(HttpStatus.NOT_FOUND, "그룹 정보를 찾을 수 없습니다."),
    IMPOSSIBLE_TO_JOIN_GROUP_ERROR(HttpStatus.BAD_REQUEST, "그룹에 들어갈 수 없습니다."),
    ALREADY_JOIN_GROUP_ERROR(HttpStatus.BAD_REQUEST, "이미 그룹에 들어가 있습니다."),

    /** Group User Error Code */
    DUPLICATE_GROUP_JOIN_ERROR(HttpStatus.BAD_REQUEST, "한개의 그룹만 참여 가능합니다."),

    /** Image Client Error Code */
    IMAGE_CLIENT_UPLOAD_ERROR(HttpStatus.BAD_REQUEST, "이미지 업로드 중 오류가 발생했습니다."),

    /** Pose Notification Error Code */
    NOT_FOUND_POSE_NOTIFICATION_ERROR(HttpStatus.NOT_FOUND, "자세 알림 정보를 찾을 수 없습니다."),

    /** Pose Layout Error Code */
    NOT_FOUND_POSE_LAYOUT_ERROR(HttpStatus.NOT_FOUND, "포즈 레이아웃을 찾을 수 없습니다."),
    ;
}
