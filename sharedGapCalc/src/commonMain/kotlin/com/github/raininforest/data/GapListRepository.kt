package com.github.raininforest.data

import com.github.raininforest.data.entity.GapListItemEntity
import com.github.raininforest.db.DBSource

class GapListRepository(private val dbSource: DBSource) {
    suspend fun gapList(): List<GapListItemEntity> = dbSource.getGapList()
}
