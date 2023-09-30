package com.github.raininforest.data.entity

data class GapDetailsEntity(
    val gapTitle: String,
    val chartData: ChartData,
    val textData: TextData,
    val warnings: List<CalculationWarnings>
)
