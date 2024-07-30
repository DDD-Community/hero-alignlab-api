package com.hero.alignlab.domain.group.model

import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.group.application.JoinCodeGenerator
import com.hero.alignlab.domain.group.domain.Group
import com.hero.alignlab.domain.group.model.request.CreateGroupRequest

class CreateGroupContext(
    private val user: AuthUser,
    private val request: CreateGroupRequest,
) {
    fun create(): Group {
        return Group(
            name = request.name,
            description = request.description,
            ownerUid = user.uid,
            isHidden = request.isHidden,
            joinCode = JoinCodeGenerator.joinCode(request.joinCode)
        )
    }
}
