package com.github.raininforest.usecases.calculator

internal data class InputParameters(
    val gapLengthInMeters: Double,
    val tableLengthInMeters: Double,
    val startHeightInMeters: Double,
    val startAngleInRads: Double,
    val finishHeightInMeters: Double,
    val finishAngleInRads: Double,
    val speedInMPS: Double,
    val startAngleInDegrees: Double,
    val finishAngleInDegrees: Double
)
