package com.github.raininforest.db

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import com.github.raininforest.DatabaseQueries
import com.github.raininforest.GapCalcDatabase
import com.github.raininforest.GapParametersTable
import com.github.raininforest.data.FINISH_ANGLE_DEFAULT_VALUE
import com.github.raininforest.data.FINISH_HEIGHT_DEFAULT_VALUE
import com.github.raininforest.data.GAP_DEFAULT_VALUE
import com.github.raininforest.data.LEGACY_UNTITLED
import com.github.raininforest.data.START_ANGLE_DEFAULT_VALUE
import com.github.raininforest.data.START_HEIGHT_DEFAULT_VALUE
import com.github.raininforest.data.START_SPEED_DEFAULT_VALUE
import com.github.raininforest.data.TABLE_DEFAULT_VALUE
import com.github.raininforest.data.entity.GapListItemEntity
import com.github.raininforest.data.entity.GapParametersEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

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

class DBSourceImpl(
    private val database: AsyncLazy<GapCalcDatabase>,
    private val dispatcher: CoroutineDispatcher
) : DBSource {

    constructor(db: GapCalcDatabase, dispatcher: CoroutineDispatcher) : this(AsyncLazy { db }, dispatcher)

    private suspend fun <T> query(block: suspend (GapCalcDatabase, DatabaseQueries) -> T): T =
        withContext(dispatcher) {
            val db = database.get()
            block(db, db.databaseQueries)
        }

    override suspend fun getGapList(): List<GapListItemEntity> = query { _, queries ->
        queries.gapList { id, title, date -> GapListItemEntity(id, title.normalizedTitle(), date) }.awaitAsList()
    }

    override suspend fun getGap(gapId: Long): GapListItemEntity? = query { _, queries ->
        queries.gap(
            gapId
        ) { id, title, date -> GapListItemEntity(id, title.normalizedTitle(), date) }.awaitAsOneOrNull()
    }

    override suspend fun getGapParameters(gapId: Long): GapParametersEntity? = query { _, queries ->
        queries
            .gapParametersByGapId(gapId) { _, gap, table, startHeight, startAngle, finishHeight, finishAngle, speed ->
                GapParametersEntity(gap, table, startHeight, startAngle, finishHeight, finishAngle, speed)
            }
            .awaitAsList()
            .firstOrNull()
    }

    override suspend fun insertGap(title: String, date: String) = query { db, queries ->
        db.transaction {
            queries.insertGap(title = title, date = date)
            val gapId = queries.lastInsertedRowId().awaitAsOne()

            queries.insertGapParameters(
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

    override suspend fun removeGap(gapId: Long) = query { db, queries ->
        // sqlite foreign keys are off by default, so ON DELETE CASCADE can't be relied on; row ids get reused
        db.transaction {
            queries.removeGapParameters(gapId)
            queries.removeGap(gapId)
        }
    }

    override suspend fun insertGapParameters(gapId: Long, parameters: GapParametersEntity) = query { _, queries ->
        queries.insertGapParameters(
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
        Unit
    }

    override suspend fun updateGapParameters(gapId: Long, parameters: GapParametersEntity) = query { _, queries ->
        queries.updateGapParameters(
            gap_id = gapId,
            gap_length = parameters.gap,
            table_length = parameters.table,
            start_angle = parameters.startAngle,
            start_height = parameters.startHeight,
            end_angle = parameters.finishAngle,
            end_height = parameters.finishHeight,
            start_speed = parameters.startSpeed
        )
        Unit
    }

    override suspend fun changeGapTitle(gapId: Long, title: String) = query { _, queries ->
        queries.updateGapTitle(title, gapId)
        Unit
    }
}

private fun String.normalizedTitle() = if (this == LEGACY_UNTITLED) "" else this
