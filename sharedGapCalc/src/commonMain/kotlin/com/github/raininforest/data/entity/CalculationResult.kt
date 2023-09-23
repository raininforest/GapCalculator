package com.github.raininforest.data.entity

import com.github.raininforest.calculator.OutputParameters

data class CalculationResult(
    val outputParameters: OutputParameters,
    val chartData: ChartData,
    val warnings: List<CalculationWarnings>
)
