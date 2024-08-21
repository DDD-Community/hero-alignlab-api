package com.hero.alignlab.domain.group.model

import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.group.application.JoinCodeGenerator
import com.hero.alignlab.domain.group.domain.Group
import com.hero.alignlab.domain.group.model.request.CreateGroupRequest
import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.InvalidRequestException

class CreateGroupContext(
    private val user: AuthUser,
    private val request: CreateGroupRequest,
) {
    fun create(): Group {
        if (request.isHidden && request.joinCode == null) {
            throw InvalidRequestException(ErrorCode.NOT_FOUND_JOIN_CODE_ERROR)
        }

        return Group(
            name = request.name,
            description = request.description,
            ownerUid = user.uid,
            isHidden = request.isHidden,
            joinCode = if (request.isHidden) {
                JoinCodeGenerator.joinCode(request.joinCode!!)
            } else {
                null
            }
        )
    }
}
