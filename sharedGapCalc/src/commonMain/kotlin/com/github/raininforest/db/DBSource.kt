package com.github.raininforest.db

import com.github.raininforest.GapCalcDatabase
import com.github.raininforest.GapParametersTable
import com.github.raininforest.GapTable
import com.github.raininforest.data.entity.GapEditEntity
import com.github.raininforest.data.entity.GapListItemEntity

interface DBSource {
    suspend fun getGapList(): List<GapListItemEntity>
    suspend fun getGap(gapId: Long): GapListItemEntity?
    suspend fun getGapParameters(gapId: Long): GapEditEntity?
    suspend fun createGap(title: String, date: String)
    suspend fun removeGap(gapId: Long)
    suspend fun insertGapParameters(gapId: Long, parameters: GapEditEntity)
    suspend fun updateGapParameters(gapId: Long, parameters: GapEditEntity)
}

class DBSourceImpl(private val db: GapCalcDatabase?) : DBSource {
    override suspend fun getGapList(): List<GapListItemEntity> =
        db?.databaseQueries
            ?.gapList { id, title, date ->
                GapListItemEntity(id, title, date)
            }
            ?.executeAsList() ?: emptyList()

    override suspend fun getGap(gapId: Long): GapListItemEntity? =
        db?.databaseQueries
            ?.gap(gapId) { id, title, date ->
                GapListItemEntity(id, title, date)
            }
            ?.executeAsOne()

    override suspend fun getGapParameters(gapId: Long): GapEditEntity? {
        return db?.databaseQueries
            ?.gapParametersByGapId(gapId) { _, gl: String, tl: String, sh: String, sa: String, eh: String, ea: String, ss: String ->
                GapEditEntity(
                    gap = gl,
                    table = tl,
                    startHeight = sh,
                    startAngle = sa,
                    finishHeight = eh,
                    finishAngle = ea,
                    startSpeed = ss
                )
            }
            ?.executeAsOne()
    }


    override suspend fun createGap(title: String, date: String) {
        db?.databaseQueries?.insertGap(
            GapTable(
                id = getGapId(title, date),
                title = title,
                date = date
            )
        )
    }

    override suspend fun removeGap(gapId: Long) {
        db?.databaseQueries?.removeGap(gapId)
    }

    override suspend fun insertGapParameters(gapId: Long, parameters: GapEditEntity) {
        db?.databaseQueries
            ?.insertGapParameters(
                gapParametersTable = GapParametersTable(
                    gap_id = gapId,
                    gap_length = parameters.gap,
                    table_length = parameters.table,
                    start_angle = parameters.startAngle,
                    start_height = parameters.startHeight,
                    end_angle = parameters.finishAngle,
                    end_height = parameters.finishHeight,
                    start_speed = parameters.startSpeed
                )
            )
    }

    override suspend fun updateGapParameters(gapId: Long, parameters: GapEditEntity) {
        db?.databaseQueries
            ?.updateGapParameters(
                gap_id = gapId,
                gap_length = parameters.gap,
                table_length = parameters.table,
                start_angle = parameters.startAngle,
                start_height = parameters.startHeight,
                end_angle = parameters.finishAngle,
                end_height = parameters.finishHeight,
                start_speed = parameters.startSpeed
            )
    }

    private fun getGapId(title: String, date: String) = "$title$date".hashCode().toLong()
}
