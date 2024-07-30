package com.hero.alignlab.exception

open class AlignlabException(
    val errorCode: ErrorCode,
    override val message: String? = errorCode.description,
    val extra: Map<String, Any>? = null,
) : RuntimeException(message ?: errorCode.description)

class NotFoundException(errorCode: ErrorCode) : AlignlabException(errorCode)

class InvalidTokenException(errorCode: ErrorCode) : AlignlabException(errorCode)

class InvalidRequestException(errorCode: ErrorCode, message: String? = null) : AlignlabException(errorCode, message)

class FailToCreateException(errorCode: ErrorCode) : AlignlabException(errorCode)

class AlreadyException(errorCode: ErrorCode) : AlignlabException(errorCode)

class NoAuthorityException(errorCode: ErrorCode) : AlignlabException(errorCode)

class FailToExecuteException(errorCode: ErrorCode) : AlignlabException(errorCode)

class RedisPubSubException(errorCode: ErrorCode) : AlignlabException(errorCode)

/** Image Exception */
class ImageUploadException : AlignlabException(ErrorCode.IMAGE_CLIENT_UPLOAD_ERROR)
