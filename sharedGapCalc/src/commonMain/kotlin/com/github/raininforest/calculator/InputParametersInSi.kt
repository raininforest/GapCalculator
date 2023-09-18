package com.github.raininforest.calculator

data class InputParametersInSI(
    val gapLengthInMeters: Double,
    val tableLengthInMeters: Double,
    val startHeightInMeters: Double,
    val startAngleInRads: Double,
    val finishHeightInMeters: Double,
    val finishAngleInRads: Double,
    val speedInMPS: Double
)
