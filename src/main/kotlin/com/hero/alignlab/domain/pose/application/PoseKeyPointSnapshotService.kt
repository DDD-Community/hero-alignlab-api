package com.hero.alignlab.domain.pose.application

import com.hero.alignlab.domain.pose.domain.PoseKeyPointSnapshot
import com.hero.alignlab.domain.pose.infrastructure.PoseKeyPointSnapshotRepository
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PoseKeyPointSnapshotService(
    private val heroNamedParameterJdbcTemplate: NamedParameterJdbcTemplate,
    private val poseKeyPointSnapshotRepository: PoseKeyPointSnapshotRepository,
) {
    @Transactional
    fun bulkSave(poseKeyPointSnapshots: List<PoseKeyPointSnapshot>) {
        val sql = """
            INSERT INTO pose_key_point_snapshot (pose_snapshot_id, position, x, y, confidence)
            VALUES (:poseSnapshotId, :position, :x, :y, :confidence)
        """

        val batchParams = SqlParameterSourceUtils.createBatch(poseKeyPointSnapshots.toTypedArray())

        heroNamedParameterJdbcTemplate.batchUpdate(sql, batchParams)
    }

    @Transactional
    fun saveAllSync(poseKeyPointSnapshots: List<PoseKeyPointSnapshot>): List<PoseKeyPointSnapshot> {
        return poseKeyPointSnapshotRepository.saveAll(poseKeyPointSnapshots)
    }
}
