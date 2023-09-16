package com.github.raininforest.db

import com.github.raininforest.GapCalcDatabase
import com.github.raininforest.GapParametersTable
import com.github.raininforest.GapTable
import com.github.raininforest.data.FINISH_ANGLE_DEFAULT_VALUE
import com.github.raininforest.data.FINISH_HEIGHT_DEFAULT_VALUE
import com.github.raininforest.data.GAP_DEFAULT_VALUE
import com.github.raininforest.data.START_ANGLE_DEFAULT_VALUE
import com.github.raininforest.data.START_HEIGHT_DEFAULT_VALUE
import com.github.raininforest.data.START_SPEED_DEFAULT_VALUE
import com.github.raininforest.data.TABLE_DEFAULT_VALUE
import com.github.raininforest.data.entity.GapListItemEntity
import com.github.raininforest.data.entity.GapParametersEntity

interface DBSource {
    suspend fun getGapList(): List<GapListItemEntity>
    suspend fun getGap(gapId: Long): GapListItemEntity?
    suspend fun insertGap(title: String, date: String)
    suspend fun changeGapTitle(gapId: Long, title: String)
    suspend fun removeGap(gapId: Long)
    suspend fun getGapParameters(gapId: Long): GapParametersEntity?
    suspend fun insertGapParameters(gapId: Long, parameters: GapParametersEntity)
    suspend fun updateGapParameters(gapId: Long, parameters: GapParametersEntity)
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
            ?.executeAsOneOrNull()

    override suspend fun getGapParameters(gapId: Long): GapParametersEntity? {
        return db?.databaseQueries
            ?.gapParametersByGapId(gapId) { _, gl: String, tl: String, sh: String, sa: String, eh: String, ea: String, ss: String ->
                GapParametersEntity(
                    gap = gl,
                    table = tl,
                    startHeight = sh,
                    startAngle = sa,
                    finishHeight = eh,
                    finishAngle = ea,
                    startSpeed = ss
                )
            }
            ?.executeAsList()?.firstOrNull()
    }


    override suspend fun insertGap(title: String, date: String) {
        db?.transaction {
            val gapId = getGapId(title, date)

            db.databaseQueries.insertGap(
                GapTable(
                    id = gapId,
                    title = title,
                    date = date
                )
            )

            db.databaseQueries.insertGapParameters(
                GapParametersTable(
                    gap_id = gapId,
                    gap_length = GAP_DEFAULT_VALUE,
                    table_length = TABLE_DEFAULT_VALUE,
                    start_height = START_HEIGHT_DEFAULT_VALUE,
                    start_angle = START_ANGLE_DEFAULT_VALUE,
                    end_height = FINISH_HEIGHT_DEFAULT_VALUE,
                    end_angle = FINISH_ANGLE_DEFAULT_VALUE,
                    start_speed = START_SPEED_DEFAULT_VALUE
                )
            )
        }
    }

    override suspend fun removeGap(gapId: Long) {
        db?.databaseQueries?.removeGap(gapId)
    }

    override suspend fun insertGapParameters(gapId: Long, parameters: GapParametersEntity) {
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

    override suspend fun updateGapParameters(gapId: Long, parameters: GapParametersEntity) {
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

    override suspend fun changeGapTitle(gapId: Long, title: String) {
        db?.databaseQueries?.updateGapTitle(title, gapId)
    }

    private fun getGapId(title: String, date: String) = "$title$date".hashCode().toLong()
}
