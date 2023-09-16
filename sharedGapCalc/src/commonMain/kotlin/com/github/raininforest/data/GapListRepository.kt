package com.github.raininforest.data

import com.github.raininforest.data.entity.GapListItemEntity
import com.github.raininforest.db.DBSource
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class GapListRepository(private val dbSource: DBSource) {
    suspend fun gapList(): List<GapListItemEntity> = dbSource.getGapList()

    suspend fun addGap() = dbSource.insertGap(title = UNTITLED, date = getDateTime())

    suspend fun removeGap(id: Long) = dbSource.removeGap(id)

    private fun getDateTime(): String {
        val localDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return "${localDateTime.date} ${localDateTime.time}"
    }
}
