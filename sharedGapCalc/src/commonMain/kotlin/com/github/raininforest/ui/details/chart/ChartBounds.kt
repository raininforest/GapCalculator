package com.github.raininforest.ui.details.chart

import com.github.raininforest.data.entity.ChartData
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

/** Visible data range of a chart, shared by the Android and iOS renderers. */
data class ChartBounds(
    val minX: Double,
    val maxX: Double,
    val minY: Double,
    val maxY: Double
) {
    val width: Double get() = maxX - minX
    val height: Double get() = maxY - minY
}

private const val MIN_SPAN = 1.0
private const val TARGET_TICKS = 10
private val NICE_STEPS = doubleArrayOf(1.0, 2.0, 5.0)

/** Bounds of all points rounded outwards to whole units, so grid lines frame the chart; y always includes 0. */
fun ChartData.bounds(): ChartBounds {
    val points = charts.flatMap { it.function }
    if (points.isEmpty()) return ChartBounds(minX = 0.0, maxX = MIN_SPAN, minY = 0.0, maxY = MIN_SPAN)

    val minX = floor(points.minOf { it.x })
    val maxX = ceil(points.maxOf { it.x })
    val minY = floor(minOf(0.0, points.minOf { it.y }))
    val maxY = ceil(maxOf(0.0, points.maxOf { it.y }))
    return ChartBounds(
        minX = minX,
        maxX = maxOf(maxX, minX + MIN_SPAN),
        minY = minY,
        maxY = maxOf(maxY, minY + MIN_SPAN)
    )
}

/** Grid/label positions in [min]..[max]: whole numbers with a 1-2-5 step that keeps about ten lines per axis. */
fun gridTicks(min: Double, max: Double, targetCount: Int = TARGET_TICKS): List<Double> {
    val span = abs(max - min)
    if (span <= 0.0) return listOf(min)
    val raw = span / targetCount
    val magnitude = 10.0.pow(floor(log10(raw)))
    val step = maxOf(1.0, NICE_STEPS.map { it * magnitude }.firstOrNull { it >= raw } ?: (10 * magnitude))
    val first = ceil(min / step) * step
    return generateSequence(first) { it + step }.takeWhile { it <= max + 1e-9 }.toList()
}
