package com.github.raininforest.calculator

import com.github.raininforest.data.entity.CalculationResult
import com.github.raininforest.data.entity.ChartData
import com.github.raininforest.data.entity.GapParametersEntity
import com.github.raininforest.data.entity.OutputParameters
import com.github.raininforest.data.entity.CalculationWarnings

interface Calculator {
    suspend fun calculate(gapInputParameters: GapParametersEntity): CalculationResult
}

internal class CalculatorImpl : Calculator {

    private companion object {
        const val BIG_GAP = 4
        const val HARD_LANDING_G = 1
    }

    override suspend fun calculate(gapInputParameters: GapParametersEntity): CalculationResult {
        val inputParams = gapInputParameters.convertToSINumbers()

        val v0 = inputParams.v0
        val v0x = inputParams.v0x
        val v0y = inputParams.v0y
        val hR = inputParams.hR

        val startRadiusMin = inputParams.startRMin
        val startRadius = inputParams.startR

        val startLength = inputParams.startLength
        val finishLength = inputParams.finishLength

        val landingParams = calculateLandingParams(
            v0x = v0x,
            v0y = v0y,
            finishHeight = inputParams.finishHeightInMeters,
            finishLength = finishLength,
            startHeight = inputParams.startHeightInMeters,
            gapLength = inputParams.gapLengthInMeters,
            finishAngle = inputParams.finishAngleInRads
        )

        //todo рассчитать данные для графиков
        //1. строим вылет (обычный с радиусом или дроп с уклоном или прямой)
        //2. приземление (обычное под углом или горизонтальное с ограничением по длине)
        //3. построение траектории полета (основная и две дополнительных)


        val outputParameters = OutputParameters(
            v0 = v0,
            rStartMin = startRadiusMin,
            rStart = startRadius,
            gLanding = landingParams.gLanding,
            hToStart = hR
        )

        val warnings = createWarningsIfNeed(
            inputParams = inputParams,
            landingParams = landingParams
        )

        return CalculationResult(
            outputParameters = outputParameters,
            chartData = ChartData(emptyList()),
            warnings = warnings
        )
    }

    private fun createWarningsIfNeed(inputParams: InputParametersInSI, landingParams: LandingParams): List<CalculationWarnings> {
        val resultList = mutableListOf<CalculationWarnings>()
        when {
            inputParams.gapLengthInMeters > BIG_GAP -> resultList.add(CalculationWarnings.BIG_GAP)
            landingParams.landingPoint > inputParams.gapLengthInMeters -> resultList.add(CalculationWarnings.EARLY_LANDING)
            landingParams.gLanding > HARD_LANDING_G -> resultList.add(CalculationWarnings.HARD_LANDING)
        }
        return resultList
    }
}
