package com.hero.alignlab.event.model

import com.hero.alignlab.domain.group.domain.Group
import java.time.LocalDateTime

sealed interface Event

open class BaseEvent(
    val publishAt: LocalDateTime = LocalDateTime.now(),
) : Event

data class CreateGroupEvent(
    val group: Group
) : BaseEvent()
