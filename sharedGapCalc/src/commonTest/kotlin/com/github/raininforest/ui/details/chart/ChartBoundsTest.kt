package com.github.raininforest.ui.details.chart

import com.github.raininforest.data.entity.Chart
import com.github.raininforest.data.entity.ChartConfig
import com.github.raininforest.data.entity.ChartData
import com.github.raininforest.data.entity.Point
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChartBoundsTest {

    private fun chart(vararg points: Pair<Double, Double>) =
        ChartData(listOf(Chart(ChartConfig.MAIN_TRACE, points.map { Point(it.first, it.second) })))

    @Test
    fun bounds_roundOutwardsAndIncludeZero() {
        val bounds = chart(-3.4 to 0.6, 8.2 to 2.3).bounds()

        assertEquals(ChartBounds(minX = -4.0, maxX = 9.0, minY = 0.0, maxY = 3.0), bounds)
    }

    @Test
    fun bounds_includeNegativeY() {
        val bounds = chart(0.0 to 1.0, 4.0 to -2.5).bounds()

        assertEquals(-3.0, bounds.minY)
        assertEquals(1.0, bounds.maxY)
    }

    @Test
    fun bounds_ofEmptyChartAreNonDegenerate() {
        val bounds = ChartData(emptyList()).bounds()

        assertTrue(bounds.width > 0 && bounds.height > 0)
    }

    @Test
    fun ticks_areWholeNumbersWithinRange() {
        assertEquals(listOf(-3.0, -2.0, -1.0, 0.0, 1.0, 2.0), gridTicks(-3.0, 2.0))
    }

    @Test
    fun ticks_useWiderStepForLargeRanges() {
        val ticks = gridTicks(0.0, 100.0)

        assertEquals(listOf(0.0, 10.0), ticks.take(2))
        assertTrue(ticks.size <= 12)
    }

    @Test
    fun ticks_ofEmptyRangeIsSinglePoint() {
        assertEquals(listOf(5.0), gridTicks(5.0, 5.0))
    }
}
