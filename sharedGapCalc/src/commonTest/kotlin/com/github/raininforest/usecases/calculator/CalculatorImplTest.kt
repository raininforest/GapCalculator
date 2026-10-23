package com.github.raininforest.usecases.calculator

import com.github.raininforest.data.entity.CalculationWarnings
import com.github.raininforest.data.entity.ChartConfig
import com.github.raininforest.data.entity.GapParametersEntity
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CalculatorImplTest {

    private val calculator = CalculatorImpl()

    private fun input(
        gap: String = "3",
        table: String = "3",
        startHeight: String = "0",
        startAngle: String = "45",
        finishHeight: String = "0",
        finishAngle: String = "45",
        startSpeed: String = "36"
    ) = GapParametersEntity(gap, table, startHeight, startAngle, finishHeight, finishAngle, startSpeed)

    @Test
    fun defaultAppValues_produceRampFinishTolerancesAndTrace() = runTest {
        val result = calculator.calculate(
            input(
                gap = "2",
                table = "2",
                startHeight = "1.2",
                startAngle = "30",
                finishHeight = "0.8",
                finishAngle = "18",
                startSpeed = "27.5"
            )
        )

        assertEquals(
            listOf(
                ChartConfig.RAMP,
                ChartConfig.MIN_R_CHART,
                ChartConfig.MAIN_GAP_ELEMENTS,
                ChartConfig.TOLERANCE_TRACE,
                ChartConfig.TOLERANCE_TRACE,
                ChartConfig.MAIN_TRACE
            ),
            result.chartData.charts.map { it.chartConfig }
        )
    }

    @Test
    fun softLanding_noWarnings() = runTest {
        val result = calculator.calculate(input(gap = "3", finishAngle = "45"))

        assertEquals(emptyList(), result.warnings)
    }

    @Test
    fun gapLongerThanRange_warnsEarlyLanding() = runTest {
        // range at 36 km/h, 45deg is ~10.2 m
        val result = calculator.calculate(input(gap = "12"))

        assertTrue(CalculationWarnings.EARLY_LANDING in result.warnings)
        assertTrue(CalculationWarnings.BIG_GAP in result.warnings)
    }

    @Test
    fun flatFinish_warnsHardLanding() = runTest {
        val result = calculator.calculate(input(finishAngle = "0"))

        assertTrue(CalculationWarnings.HARD_LANDING in result.warnings)
    }

    @Test
    fun outputParameters_matchPhysics() = runTest {
        val out = calculator.calculate(input(finishAngle = "0")).outputParameters

        assertEquals(10.0, out.v0, 1e-9)
        assertEquals(out.v0x, out.v0y, 1e-6)
        assertEquals(100.0 / (2 * g), out.hR, 1e-9)
    }

    @Test
    fun trace_startsAtLipAndHasMonotonicX() = runTest {
        val trace = calculator.calculate(input(startHeight = "1.2")).chartData.charts
            .single { it.chartConfig == ChartConfig.MAIN_TRACE }.function

        assertEquals(0.0, trace.first().x)
        assertEquals(1.2, trace.first().y, 1e-9)
        assertTrue(trace.zipWithNext().all { (a, b) -> b.x > a.x })
        assertTrue(trace.last().x < 3.0 + 4.0)
    }

    @Test
    fun drop_hasDropElementInsteadOfRadiusCharts() = runTest {
        val charts = calculator.calculate(input(startAngle = "-10", startHeight = "1")).chartData.charts

        assertEquals(
            listOf(
                ChartConfig.MAIN_GAP_ELEMENTS,
                ChartConfig.MAIN_GAP_ELEMENTS,
                ChartConfig.TOLERANCE_TRACE,
                ChartConfig.TOLERANCE_TRACE,
                ChartConfig.MAIN_TRACE
            ),
            charts.map { it.chartConfig }
        )
        assertTrue(charts.none { it.chartConfig == ChartConfig.MIN_R_CHART })
    }

    @Test
    fun toleranceTraces_flyHigherAndLowerThanMainTrace() = runTest {
        val charts = calculator.calculate(
            input(startHeight = "1", startAngle = "30", startSpeed = "35")
        ).chartData.charts
        val apexes = charts.filter { it.chartConfig == ChartConfig.TOLERANCE_TRACE }
            .map { chart -> chart.function.maxOf { it.y } }
        val mainApex = charts.single { it.chartConfig == ChartConfig.MAIN_TRACE }.function.maxOf { it.y }

        assertEquals(2, apexes.size)
        assertTrue(apexes.min() < mainApex && mainApex < apexes.max())
    }

    @Test
    fun traces_stopAtBottomOfTheChart() = runTest {
        val trace = calculator.calculate(input(gap = "12")).chartData.charts
            .single { it.chartConfig == ChartConfig.MAIN_TRACE }.function

        assertTrue(trace.all { it.y >= -1.0 })
        assertTrue(trace.last().y < 0.0)
    }

    @Test
    fun rampsAreFilledAndOtherLinesAreNot() {
        assertTrue(ChartConfig.RAMP.fillHexColor != null)
        assertTrue(ChartConfig.MIN_R_CHART.fillHexColor != null)
        assertTrue(ChartConfig.MAIN_TRACE.fillHexColor == null)
    }

    @Test
    fun verticalLaunch_traceIsTwoPointsAtZeroX() = runTest {
        val charts = calculator.calculate(input(startAngle = "90")).chartData.charts
        assertTrue(charts.none { it.chartConfig == ChartConfig.TOLERANCE_TRACE })
        val trace = calculator.calculate(input(startAngle = "90")).chartData.charts
            .single { it.chartConfig == ChartConfig.MAIN_TRACE }.function

        assertEquals(2, trace.size)
        assertTrue(trace.all { it.x == 0.0 })
        assertTrue(trace[1].y > trace[0].y)
    }

    @Test
    fun finishChart_endsAtGapLengthPlusTable() = runTest {
        val finish = calculator.calculate(input(gap = "3", table = "2", finishHeight = "0.5")).chartData.charts
            .filter { it.chartConfig == ChartConfig.MAIN_GAP_ELEMENTS }.last().function

        assertEquals(1.0, finish[0].x, 1e-9)
        assertEquals(3.0, finish[1].x, 1e-9)
        assertTrue(finish.take(2).all { it.y == 0.5 })
    }

    @Test
    fun invalidInput_returnsFallbackWithoutCharts() = runTest {
        val result = calculator.calculate(input(gap = "abc"))

        assertTrue(result.chartData.charts.isEmpty())
        assertTrue(result.warnings.isEmpty())
        assertEquals(0.0, result.outputParameters.v0)
    }

    @Test
    fun cancellation_isNotSwallowedAsFallback() = runTest {
        val job = launch(start = CoroutineStart.UNDISPATCHED) {
            CalculatorImpl(dispatcher = StandardTestDispatcher(testScheduler)).calculate(input())
        }

        job.cancel()
        job.join()

        assertTrue(job.isCancelled)
    }
}
