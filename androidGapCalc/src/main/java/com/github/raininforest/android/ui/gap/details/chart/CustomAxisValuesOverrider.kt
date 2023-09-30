package com.github.raininforest.android.ui.gap.details.chart

import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.extension.ceil
import com.patrykandpatrick.vico.core.extension.floor

class CustomAxisValuesOverrider : AxisValuesOverrider<ChartEntryModel> {

    private companion object {
        const val MIN_Y = -2f
    }

    override fun getMaxX(model: ChartEntryModel): Float? {
        return model.maxX.ceil
    }

    override fun getMaxY(model: ChartEntryModel): Float? {
        return maxOf(model.maxX.ceil, model.maxY.ceil)
    }

    override fun getMinX(model: ChartEntryModel): Float? {
        return model.minX.floor
    }

    override fun getMinY(model: ChartEntryModel): Float = MIN_Y
}
