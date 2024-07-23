package com.hero.alignlab.domain.team.application

import com.hero.alignlab.common.extension.executes
import com.hero.alignlab.config.database.TransactionTemplates
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.team.domain.Team
import com.hero.alignlab.domain.team.infrastructure.TeamRepository
import com.hero.alignlab.domain.team.model.request.CreateTeamRequest
import com.hero.alignlab.domain.team.model.response.CreateTeamResponse
import com.hero.alignlab.event.model.CreateTeamEvent
import com.hero.alignlab.exception.ErrorCode
import com.hero.alignlab.exception.InvalidRequestException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class TeamService(
    private val teamRepository: TeamRepository,
    private val txTemplates: TransactionTemplates,
    private val publisher: ApplicationEventPublisher,
) {
    suspend fun createTeam(user: AuthUser, request: CreateTeamRequest): CreateTeamResponse {
        if (existsByName(request.name)) {
            throw InvalidRequestException(ErrorCode.DUPLICATE_TEAM_NAME_ERROR)
        }

        val createdTeam = txTemplates.writer.executes {
            val team = teamRepository.save(
                Team(
                    name = request.name,
                    description = request.description,
                    ownerUid = user.uid
                )
            )

            publisher.publishEvent(CreateTeamEvent(team))

            team
        }

        return CreateTeamResponse.from(createdTeam)
    }

    suspend fun existsByName(name: String): Boolean {
        return withContext(Dispatchers.IO) {
            teamRepository.existsByName(name)
        }
    }
}
