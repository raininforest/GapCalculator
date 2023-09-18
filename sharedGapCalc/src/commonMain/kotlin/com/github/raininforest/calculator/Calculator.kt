package com.github.raininforest.calculator

import com.github.raininforest.data.entity.CalculationResult
import com.github.raininforest.data.entity.ChartData
import com.github.raininforest.data.entity.GapParametersEntity
import com.github.raininforest.data.entity.OutputParameters

interface Calculator {
    suspend fun calculate(gapInputParameters: GapParametersEntity): CalculationResult
}

internal class CalculatorImpl : Calculator {

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

        //todo реакция на плохие параметры приземления

        //todo реакция на большой gap>6

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

        return CalculationResult(
            outputParameters = outputParameters,
            chartData = ChartData(emptyList()) //todo
        )
    }
}
