package com.github.raininforest.data

import com.github.raininforest.data.entity.ChartData
import com.github.raininforest.data.entity.GapDetailsEntity
import com.github.raininforest.data.entity.TextData

class GapDetailsRepository {
    suspend fun getGapDetails(gapId: String): GapDetailsEntity {
        //todo just for test
        return GapDetailsEntity(
            gapTitle = "Untitled gap 1 test",
            chartData = ChartData(emptyList()),
            textData = TextData(emptyList(), emptyList())
        )
    }
}