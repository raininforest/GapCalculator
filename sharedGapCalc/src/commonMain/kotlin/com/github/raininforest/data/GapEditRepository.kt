package com.github.raininforest.data

import com.github.raininforest.data.entity.GapEditEntity

class GapEditRepository {
    suspend fun gapEditParameters(gapId: String): GapEditEntity =
        // todo just for test
        GapEditEntity(
            gapTitle = "Untitled gap 1",
            gap = "2.0",
            table = "2.0",
            startAngle = "22",
            startHeight = "1.2",
            finishAngle = "17",
            finishHeight = "0.8",
            startSpeed = "37.2"
        )
}
