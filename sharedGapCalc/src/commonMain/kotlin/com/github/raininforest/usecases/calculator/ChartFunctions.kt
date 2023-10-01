package com.github.raininforest.usecases.calculator

import kotlin.math.pow
import kotlin.math.sqrt

internal fun flightFx(x: Double, v0x: Double, v0y: Double): Double {
    return if (v0x != 0.0) {
        ((v0y * (x / v0x)) - ((g / 2) * ((x / v0x).pow(2))))
    } else {
        0.0
    }
}

internal fun startCircleFx(x: Double, radius: Double, xRange: Double): Double {
    return -sqrt(radius * radius - (x + xRange).pow(2)) + radius
}
