package com.github.raininforest.usecases.calculator

import com.github.raininforest.data.entity.CalculationResult
import com.github.raininforest.data.entity.Chart
import com.github.raininforest.data.entity.ChartConfig
import com.github.raininforest.data.entity.ChartData
import com.github.raininforest.data.entity.GapParametersEntity
import com.github.raininforest.data.entity.OutputParameters
import com.github.raininforest.data.entity.Point
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.tan

internal class CalculatorImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : Calculator {

    private companion object {
        const val DEFAULT_START_LENGTH = 4.0
        const val DEFAULT_FINISH_LENGTH = 4.0
        const val X_STEP = 0.1

        // besides the main flight path, paths with the takeoff angle off by this much are drawn (the old app did too)
        const val ANGLE_TOLERANCE_DEGREES = 5.0
        const val VERTICAL_LAUNCH_DEGREES = 89

        val FALLBACK_RESULT = CalculationResult(
            outputParameters = OutputParameters(
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0,
                0.0
            ),
            chartData = ChartData(emptyList()),
            warnings = emptyList()
        )
    }

    override suspend fun calculate(gapInputParameters: GapParametersEntity): CalculationResult =
        withContext(dispatcher) {
            try {
                innerCalculate(gapInputParameters)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // malformed input (non-numeric text, division by zero, ...): show empty result
                FALLBACK_RESULT
            }
        }

    private fun innerCalculate(gapInputParameters: GapParametersEntity): CalculationResult {
        val inputParams = gapInputParameters.convertToSINumbers()

        val landingParams = calculateLandingParams(inParams = inputParams)

        val outputParameters = calculateOutParams(
            inParams = inputParams,
            landingParams = landingParams
        )

        val chartData = buildChartData(
            inParams = inputParams,
            outParams = outputParameters
        )

        val warnings = createWarningsIfNeed(
            inParams = inputParams,
            landingParams = landingParams
        )

        return CalculationResult(
            outputParameters = outputParameters,
            chartData = chartData,
            warnings = warnings
        )
    }

    private fun buildChartData(
        inParams: InputParameters,
        outParams: OutputParameters
    ): ChartData {
        val resultCharts = mutableListOf<Chart>()

        if (inParams.isNotDrop && inParams.startAngleInDegrees < VERTICAL_LAUNCH_DEGREES) {
            resultCharts.add(buildRStart(inParams, outParams))
            resultCharts.add(buildRMinStart(inParams, outParams))
        } else if (inParams.isDrop) {
            resultCharts.add(buildDrop(inParams))
        }

        resultCharts.add(buildFinish(inParams))

        if (inParams.startAngleInDegrees < VERTICAL_LAUNCH_DEGREES) {
            listOf(-ANGLE_TOLERANCE_DEGREES, ANGLE_TOLERANCE_DEGREES)
                .map { inParams.startAngleInDegrees + it }
                .filter { it < VERTICAL_LAUNCH_DEGREES }
                .map { inParams.copy(startAngleInDegrees = it, startAngleInRads = it.degreesToRads()) }
                .forEach { resultCharts.add(buildTrace(it, ChartConfig.TOLERANCE_TRACE)) }
        }
        resultCharts.add(buildTrace(inParams, ChartConfig.MAIN_TRACE))

        return ChartData(resultCharts)
    }

    private fun buildTrace(inParams: InputParameters, config: ChartConfig): Chart {
        val resultFunction = mutableListOf<Point>()
        val endX = DEFAULT_FINISH_LENGTH + inParams.gapLengthInMeters
        val bottomY = chartBottom(inParams)

        if (inParams.startAngleInDegrees < VERTICAL_LAUNCH_DEGREES) {
            var x = 0.0
            while (x < endX) {
                val y = inParams.startHeightInMeters + flightFx(x = x, v0x = inParams.v0x, v0y = inParams.v0y)
                if (y < bottomY) break // below the visible area, the path is not interesting any more
                resultFunction.add(Point(x = x, y = y))
                x += X_STEP
            }
        } else {
            // вылет вертикально вверх
            resultFunction.add(Point(x = 0.0, y = inParams.startHeightInMeters))
            resultFunction.add(
                Point(x = 0.0, y = inParams.startHeightInMeters + (inParams.v0y.pow(2)) / (2 * g))
            )
        }

        return Chart(chartConfig = config, function = resultFunction)
    }

    private fun chartBottom(inParams: InputParameters): Double =
        if (inParams.finishHeightInMeters < 0) {
            -ceil(abs(inParams.finishHeightInMeters)) - 2
        } else {
            -1.0
        }

    private fun buildRStart(inParams: InputParameters, outParams: OutputParameters) =
        Chart(
            chartConfig = ChartConfig.RAMP,
            function = buildRFunction(
                xRange = inParams.startLength,
                radius = outParams.startRadius
            )
        )

    private fun buildRMinStart(inParams: InputParameters, outParams: OutputParameters) =
        Chart(
            chartConfig = ChartConfig.MIN_R_CHART,
            function = buildRFunction(
                xRange = inParams.startLengthMin,
                radius = outParams.startRadiusMin
            )
        )

    private fun buildRFunction(xRange: Double, radius: Double): List<Point> {
        val resultFunction = mutableListOf<Point>()

        val startX = -xRange
        val endX = 0.0

        var x = startX
        while (x < endX) {
            resultFunction.add(
                Point(
                    x = x,
                    y = startCircleFx(
                        x = x,
                        radius = radius,
                        xRange = xRange
                    )
                )
            )
            x += X_STEP
        }

        return resultFunction
    }

    private fun buildDrop(inParams: InputParameters): Chart {
        val resultFunction = mutableListOf<Point>()

        val startX = -DEFAULT_START_LENGTH
        val endX = 0.0

        resultFunction.add(
            Point(
                x = startX,
                y = (inParams.startHeightInMeters + abs(startX * tan(inParams.startAngleInRads)))
            )
        )
        resultFunction.add(
            Point(
                x = endX,
                y = inParams.startHeightInMeters
            )
        )

        return Chart(
            chartConfig = ChartConfig.MAIN_GAP_ELEMENTS,
            function = resultFunction
        )
    }

    private fun buildFinish(inParams: InputParameters): Chart {
        val resultFunction = mutableListOf<Point>()

        val startX = inParams.gapLengthInMeters - inParams.tableLengthInMeters
        val tableEndX = inParams.gapLengthInMeters
        val endX = DEFAULT_FINISH_LENGTH + inParams.gapLengthInMeters
        val minY = chartBottom(inParams)

        resultFunction.add(
            Point(
                x = startX,
                y = inParams.finishHeightInMeters
            )
        )
        resultFunction.add(
            Point(
                x = tableEndX,
                y = inParams.finishHeightInMeters
            )
        )

        if (inParams.finishAngleInRads == 0.0) {
            resultFunction.add(
                Point(
                    x = endX,
                    y = inParams.finishHeightInMeters
                )
            )
        } else {
            resultFunction.add(
                Point(
                    x = (
                        inParams.gapLengthInMeters + (abs(minY) + inParams.finishHeightInMeters) /
                            abs(tan(inParams.finishAngleInRads))
                        ),
                    y = minY
                )
            )
        }

        return Chart(
            chartConfig = ChartConfig.MAIN_GAP_ELEMENTS,
            function = resultFunction
        )
    }
}
