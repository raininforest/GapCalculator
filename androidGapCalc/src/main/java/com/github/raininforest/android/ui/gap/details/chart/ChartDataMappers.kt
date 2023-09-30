package com.github.raininforest.android.ui.gap.details.chart

import com.github.raininforest.data.entity.ChartData
import com.github.raininforest.data.entity.Point
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.composed.ComposedChartEntryModelProducer

internal fun getChartEntryModelProducer(chartData: ChartData): ComposedChartEntryModelProducer<ChartEntryModel> {
    val chartEntryModelProducers = chartData.charts
        .map { ChartEntryModelProducer() }

    return ComposedChartEntryModelProducer(chartEntryModelProducers)
}

internal fun ComposedChartEntryModelProducer<ChartEntryModel>.setData(chartData: ChartData) {
    chartModelProducers.forEachIndexed { i, chartModelProducer ->
        (chartModelProducer as? ChartEntryModelProducer)?.setEntries(chartData.charts[i].function.toEntries())
    }
}

private fun List<Point>.toEntries(): List<ChartEntry> =
    map { FloatEntry(it.x.toFloat(), it.y.toFloat()) }
