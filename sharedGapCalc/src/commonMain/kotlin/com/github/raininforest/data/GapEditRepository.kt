package com.github.raininforest.data

import com.github.raininforest.data.entity.GapEditEntity
import com.github.raininforest.db.DBSource

class GapEditRepository(private val dbSource: DBSource) {
    suspend fun gapEditParameters(gapId: Long): GapEditEntity =
        // todo just for test
        GapEditEntity(
            gap = "2.0",
            table = "2.0",
            startHeight = "1.2",
            startAngle = "22",
            finishHeight = "0.8",
            finishAngle = "17",
            startSpeed = "37.2",
        )

    suspend fun gapTitle(gapId: Long): String = dbSource.getGap(gapId)?.title.orEmpty()
}
