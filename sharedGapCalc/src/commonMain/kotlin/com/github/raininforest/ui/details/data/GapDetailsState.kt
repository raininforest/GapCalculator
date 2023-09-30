package com.github.raininforest.ui.details.data

import com.github.raininforest.data.entity.ChartData
import com.github.raininforest.data.entity.TextData

sealed class GapDetailsState {
    data class GapDetailsData(
        val gapTitle: String,
        val chartData: ChartData,
        val textData: TextData,
        val warnings: List<String>
    ) : GapDetailsState()

    data object GapDetailsEmpty: GapDetailsState()
}

