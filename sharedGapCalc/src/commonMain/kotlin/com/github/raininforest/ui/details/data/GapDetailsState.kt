package com.github.raininforest.ui.details.data

import com.github.raininforest.data.entity.CalculationWarnings
import com.github.raininforest.data.entity.ChartData
import com.github.raininforest.data.entity.TextData

sealed class GapDetailsState {
    data class GapDetailsData(
        val gapTitle: String,
        val chartData: ChartData,
        val textData: TextData,
        val warnings: List<CalculationWarnings>
    ) : GapDetailsState()

    /** Nothing has been loaded yet; unlike [GapDetailsEmpty] it must not show a "no data" message. */
    data object Loading : GapDetailsState()
    data object GapDetailsEmpty : GapDetailsState()
}
