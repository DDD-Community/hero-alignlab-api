package com.hero.alignlab.exception

open class HeroException(
    val errorCode: ErrorCode,
    override val message: String? = errorCode.description,
    val extra: Map<String, Any>? = null,
) : RuntimeException(message ?: errorCode.description)

class NotFoundException(errorCode: ErrorCode) : HeroException(errorCode)

class InvalidTokenException(errorCode: ErrorCode) : HeroException(errorCode)

class InvalidRequestException(errorCode: ErrorCode, message: String? = null) : HeroException(errorCode, message)

class FailToCreateException(errorCode: ErrorCode) : HeroException(errorCode)

class AlreadyException(errorCode: ErrorCode) : HeroException(errorCode)

class NoAuthorityException(errorCode: ErrorCode) : HeroException(errorCode)

class FailToExecuteException(errorCode: ErrorCode) : HeroException(errorCode)

class RedisPubSubException(errorCode: ErrorCode) : HeroException(errorCode)

/** Image Exception */
class ImageUploadException : HeroException(ErrorCode.IMAGE_CLIENT_UPLOAD_ERROR)
