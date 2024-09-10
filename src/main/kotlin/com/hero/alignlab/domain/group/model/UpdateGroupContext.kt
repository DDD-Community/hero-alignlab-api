package com.hero.alignlab.domain.group.model

import com.hero.alignlab.domain.group.application.JoinCodeGenerator
import com.hero.alignlab.domain.group.domain.Group
import com.hero.alignlab.domain.group.model.request.UpdateGroupRequest
import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.InvalidRequestException

class UpdateGroupContext(
    private val before: Group,
    private val request: UpdateGroupRequest,
) {
    fun update(): Group {
        if (request.isHidden && request.joinCode == null) {
            throw InvalidRequestException(ErrorCode.NOT_FOUND_JOIN_CODE_ERROR)
        }

        if (request.name.length > 16) {
            throw InvalidRequestException(ErrorCode.OVER_RANGE_GROUP_NAME_ERROR)
        }

        return this.before.apply {
            this.name = request.name
            this.description = request.description
            this.isHidden = request.isHidden
            this.joinCode = if (request.isHidden) {
                JoinCodeGenerator.joinCode(request.joinCode!!)
            } else {
                null
            }
            this.userCapacity = request.userCapacity ?: 30
        }
    }
}
