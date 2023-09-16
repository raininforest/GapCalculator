package com.github.raininforest.data

import com.github.raininforest.data.entity.GapParametersEntity
import com.github.raininforest.db.DBSource

class GapEditRepository(private val dbSource: DBSource) {
    suspend fun gapEditParameters(gapId: Long): GapParametersEntity? = dbSource.getGapParameters(gapId)

    suspend fun changeGapParameters(gapId: Long, parameters: GapParametersEntity) =
        dbSource.updateGapParameters(gapId, parameters)

    suspend fun changeGapTitle(gapId: Long, title: String) = dbSource.changeGapTitle(gapId, title)

    suspend fun gapTitle(gapId: Long): String = dbSource.getGap(gapId)?.title.orEmpty()
}
