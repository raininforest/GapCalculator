package com.github.raininforest.usecases.calculator

import com.github.raininforest.data.entity.CalculationResult
import com.github.raininforest.data.entity.GapParametersEntity

interface Calculator {
    suspend fun calculate(gapInputParameters: GapParametersEntity): CalculationResult
}

