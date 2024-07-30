package com.hero.alignlab.domain.group.model

import com.hero.alignlab.domain.group.application.JoinCodeGenerator
import com.hero.alignlab.domain.group.domain.Group
import com.hero.alignlab.domain.group.model.request.UpdateGroupRequest

class UpdateGroupContext(
    private val before: Group,
    private val request: UpdateGroupRequest,
) {
    fun update(): Group {
        return this.before.apply {
            this.name = request.name
            this.description = request.description
            this.isHidden = request.isHidden
            this.joinCode = JoinCodeGenerator.joinCode(request.joinCode)
        }
    }
}
