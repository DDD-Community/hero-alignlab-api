package com.hero.alignlab.domain.pose.application

import com.hero.alignlab.common.model.AlignlabPageRequest
import com.hero.alignlab.domain.auth.model.AuthUser
import com.hero.alignlab.domain.pose.domain.PoseCount
import com.hero.alignlab.domain.pose.infrastructure.PoseCountRepository
import com.hero.alignlab.domain.pose.infrastructure.model.PostCountSearchSpec
import com.hero.alignlab.domain.pose.model.request.PoseSearchRequest
import com.hero.alignlab.domain.pose.model.response.PoseCountResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class PoseCountService(
    private val poseCountRepository: PoseCountRepository,
) {
    @Transactional
    fun saveSync(poseCount: PoseCount): PoseCount {
        return poseCountRepository.save(poseCount)
    }

    suspend fun findByDateOrNull(date: LocalDate): PoseCount? {
        return withContext(Dispatchers.IO) {
            poseCountRepository.findByDate(date)
        }
    }

    suspend fun search(
        user: AuthUser,
        request: PoseSearchRequest,
        pageRequest: AlignlabPageRequest
    ): Page<PoseCountResponse> {
        val pageable = pageRequest.toDefault()
        val searchSpec = PostCountSearchSpec(
            uid = user.uid,
            fromDate = request.fromDate,
            toDate = request.toDate,
        )

        return search(searchSpec, pageable)
            .map { count -> PoseCountResponse(count.date, count.totalCount.count) }
    }

    suspend fun search(spec: PostCountSearchSpec, pageable: Pageable): Page<PoseCount> {
        return withContext(Dispatchers.IO) {
            poseCountRepository.search(spec, pageable)
        }
    }
}
