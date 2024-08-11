package com.hero.alignlab.domain.pose.application

import com.hero.alignlab.common.model.HeroPageRequest
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

    @Transactional
    fun saveAllSync(poseCounts: List<PoseCount>): List<PoseCount> {
        return poseCountRepository.saveAll(poseCounts)
    }

    suspend fun findByUidAndDateOrNull(uid: Long, date: LocalDate): PoseCount? {
        return withContext(Dispatchers.IO) {
            poseCountRepository.findByUidAndDate(uid, date)
        }
    }

    suspend fun search(
        user: AuthUser,
        request: PoseSearchRequest,
        pageRequest: HeroPageRequest
    ): Page<PoseCountResponse> {
        val pageable = pageRequest.toDefault()
        val searchSpec = PostCountSearchSpec(
            uid = user.uid,
            fromDate = request.fromDate,
            toDate = request.toDate,
        )

        return search(searchSpec, pageable)
            .map { count ->
                val counts = count.totalCount.count
                    .map { (type, count) -> PoseCountResponse.PoseCountModel(type, count) }

                PoseCountResponse(count.date, counts)
            }
    }

    suspend fun search(spec: PostCountSearchSpec, pageable: Pageable): Page<PoseCount> {
        return withContext(Dispatchers.IO) {
            poseCountRepository.search(spec, pageable)
        }
    }

    suspend fun getDailyPoseCount(user: AuthUser, date: LocalDate?): PoseCountResponse {
        val targetDate = date ?: LocalDate.now()

        val poseCount = findByUidAndDateOrNull(user.uid, targetDate)

        return PoseCountResponse(
            date = targetDate,
            count = when (poseCount == null) {
                true -> emptyList()
                false -> poseCount.totalCount.count
                    .map { (type, count) -> PoseCountResponse.PoseCountModel(type, count) }
            }
        )
    }

    suspend fun findAllByUidIn(uids: List<Long>): List<PoseCount> {
        return withContext(Dispatchers.IO) {
            poseCountRepository.findAllByUidIn(uids)
        }
    }
}
