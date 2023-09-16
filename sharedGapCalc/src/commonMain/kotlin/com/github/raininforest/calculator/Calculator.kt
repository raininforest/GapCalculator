package com.github.raininforest.calculator

import com.github.raininforest.data.entity.CalculationResult
import com.github.raininforest.data.entity.ChartData
import com.github.raininforest.data.entity.GapParametersEntity
import com.github.raininforest.data.entity.OutputParameters

interface Calculator {
    suspend fun calculate(gapInputParameters: GapParametersEntity): CalculationResult
}

internal class CalculatorImpl: Calculator {
    override suspend fun calculate(gapInputParameters: GapParametersEntity): CalculationResult {
        return CalculationResult( // todo
            outputParameters = OutputParameters("", "", "", "", ""),
            chartData = ChartData(emptyList())
        )
    }
}
