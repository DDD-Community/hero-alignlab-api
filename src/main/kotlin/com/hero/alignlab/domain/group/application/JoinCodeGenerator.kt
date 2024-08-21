package com.hero.alignlab.domain.group.application

import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.InvalidRequestException

object JoinCodeGenerator {
    private val codePattern = Regex("^[0-9]{4}$")

    fun joinCode(code: String): String {
        return when (codePattern.matches(code)) {
            true -> code
            else -> {
                throw InvalidRequestException(ErrorCode.INVALID_JOIN_CODE_ERROR)
            }
        }
    }
}
