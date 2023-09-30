package com.github.raininforest.android.ui.gap.details.chart

import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.draw.ChartDrawContext
import com.patrykandpatrick.vico.core.chart.values.ChartValues
import com.patrykandpatrick.vico.core.context.MeasureContext
import com.patrykandpatrick.vico.core.extension.half
import kotlin.math.abs
import kotlin.math.max

class CustomVerticalItemPlacer: AxisItemPlacer.Vertical {
    override fun getBottomVerticalAxisInset(
        verticalLabelPosition: VerticalAxis.VerticalLabelPosition,
        maxLabelHeight: Float,
        maxLineThickness: Float,
    ): Float = when (verticalLabelPosition) {
        VerticalAxis.VerticalLabelPosition.Top -> maxLineThickness
        VerticalAxis.VerticalLabelPosition.Center -> (maxOf(maxLabelHeight, maxLineThickness) + maxLineThickness).half
        VerticalAxis.VerticalLabelPosition.Bottom -> maxLabelHeight + maxLineThickness.half
    }

    override fun getHeightMeasurementLabelValues(
        context: MeasureContext,
        position: AxisPosition.Vertical,
    ): List<Float> {
        val chartValues = context.chartValuesManager.getChartValues(position)
        return listOf(chartValues.minY, (chartValues.minY + chartValues.maxY).half, chartValues.maxY)
    }

    override fun getLabelValues(
        context: ChartDrawContext,
        axisHeight: Float,
        maxLabelHeight: Float,
        position: AxisPosition.Vertical
    ): List<Float> {
        return getWidthMeasurementLabelValues(context, axisHeight, maxLabelHeight, position)
    }

    override fun getTopVerticalAxisInset(
        verticalLabelPosition: VerticalAxis.VerticalLabelPosition,
        maxLabelHeight: Float,
        maxLineThickness: Float
    ): Float {
        return when (verticalLabelPosition) {
            VerticalAxis.VerticalLabelPosition.Top -> maxLabelHeight + maxLineThickness.half
            VerticalAxis.VerticalLabelPosition.Center -> (max(maxLabelHeight, maxLineThickness) + maxLineThickness).half
            VerticalAxis.VerticalLabelPosition.Bottom -> maxLineThickness
        }
    }

    override fun getWidthMeasurementLabelValues(
        context: MeasureContext,
        axisHeight: Float,
        maxLabelHeight: Float,
        position: AxisPosition.Vertical,
    ): List<Float> {
        val chartValues = context.chartValuesManager.getChartValues(position)
        return getSimpleLabelValues(chartValues)
    }

    private fun getSimpleLabelValues(chartValues: ChartValues): List<Float> {
        val values = mutableListOf(chartValues.minY)
        val extraItemCount = (abs(chartValues.minY) + chartValues.maxY).toInt()
        val step = 1f
        repeat(extraItemCount) { values += chartValues.minY + (it + 1) * step }
        return values
    }
}
