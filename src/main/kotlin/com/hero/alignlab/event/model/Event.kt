package com.hero.alignlab.event.model

import com.hero.alignlab.domain.team.domain.Team
import java.time.LocalDateTime

sealed interface Event

open class BaseEvent(
    val publishAt: LocalDateTime = LocalDateTime.now(),
) : Event

data class CreateTeamEvent(
    val team: Team
) : BaseEvent()
