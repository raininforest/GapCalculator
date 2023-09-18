package com.github.raininforest.calculator

import com.github.raininforest.data.entity.GapParametersEntity
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

private const val METERS_IN_KILOMETER = 1000
private const val SECONDS_IN_HOUR = 3600
private const val HALF_CIRCLE_DEGREES = 180

internal fun GapParametersEntity.convertToSINumbers(): InputParametersInSI =
    InputParametersInSI(
        gapLengthInMeters = gap.toDouble(),
        tableLengthInMeters = table.toDouble(),
        startHeightInMeters = startHeight.toDouble(),
        startAngleInRads = startAngle.toDouble().degreesToRads(),
        finishHeightInMeters = finishHeight.toDouble(),
        finishAngleInRads = finishAngle.toDouble().degreesToRads(),
        speedInMPS = startSpeed.toDouble().kmHtoSI()
    )

internal val InputParametersInSI.v0: Double
    get() = if (!isDrop) {
        sqrt(speedInMPS * speedInMPS - 2 * g * startHeightInMeters)
    } else {
        speedInMPS
    }

internal val InputParametersInSI.v0x: Double
    get() = v0 * cos(startAngleInRads)

internal val InputParametersInSI.v0y: Double
    get() = v0 * sin(startAngleInRads)

internal val InputParametersInSI.hR: Double
    get() = (speedInMPS * speedInMPS) / (2 * g)

internal val InputParametersInSI.startRMin: Double
    get() = (speedInMPS * speedInMPS) / (2 * g)

internal val InputParametersInSI.startR: Double
    get() = startHeightInMeters / (1 - cos(startAngleInRads))

internal val InputParametersInSI.startLength: Double
    get() = startR * sin(startAngleInRads)

internal val InputParametersInSI.finishLength: Double
    get() = abs(finishHeightInMeters / tan(finishAngleInRads))

internal val InputParametersInSI.isDrop: Boolean
    get() = startAngleInRads <= 0

internal fun calculateLandingParams(
    v0x: Double,
    v0y: Double,
    finishHeight: Double,
    finishLength: Double,
    startHeight: Double,
    gapLength: Double,
    finishAngle: Double
): LandingParams {
    val a: Double = -g / (2 * v0x * v0x)
    val b: Double = v0y / v0x + finishHeight / finishLength
    val c: Double = -(finishHeight + finishHeight * gapLength / finishLength) + startHeight
    val diskriminant: Double = b * b - 4 * a * c

    val gLanding: Double
    val landingPoint: Double

    if (diskriminant < 0) {
        gLanding = 0.0
        landingPoint = 0.0
    } else {
        //точка пересечения траектории полёта и линии приземления (для расчёта жесткости приземления)
        val xK = findRoot(diskriminant, a, b)
        //скорость в точке касания приземления
        val vk = sqrt(v0x * v0x + (v0y - g * xK / v0x).pow(2))
        //угол касания
        val fi = atan((v0y - g * xK / v0x) / v0x)
        // жесткость приземления, как эквивалентная высота дропа на плоскач
        val hEqui = (vk * sin(abs(abs(fi) - finishAngle))).pow(2) / (2 * g)

        gLanding = hEqui
        landingPoint = xK
    }

    return LandingParams(landingPoint = landingPoint, gLanding = gLanding)
}

internal fun findRoot(diskriminant: Double, a: Double, b: Double): Double = (-b - sqrt(diskriminant)) / (2 * a)

internal fun Double.kmHtoSI(): Double = this * METERS_IN_KILOMETER / SECONDS_IN_HOUR

internal fun Double.degreesToRads(): Double = this * PI / HALF_CIRCLE_DEGREES