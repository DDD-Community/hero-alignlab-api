package com.hero.alignlab.domain.group.application

import java.util.*

object JoinCodeGenerator {
    fun joinCode(code: String? = null): String {
        return code ?: UUID.randomUUID().toString().replace("-", "")
    }
}
