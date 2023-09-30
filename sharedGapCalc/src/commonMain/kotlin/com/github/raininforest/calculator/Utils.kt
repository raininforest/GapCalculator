package com.github.raininforest.calculator

import com.github.raininforest.data.entity.CalculationWarnings
import com.github.raininforest.data.entity.GapParametersEntity
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

private const val METERS_IN_KILOMETER = 1000
private const val SECONDS_IN_HOUR = 3600
private const val HALF_CIRCLE_DEGREES = 180
private const val BIG_GAP = 4.0
private const val HARD_LANDING_G = 1.0

internal fun GapParametersEntity.convertToSINumbers(): InputParameters =
    InputParameters(
        gapLengthInMeters = gap.toDouble(),
        tableLengthInMeters = table.toDouble(),
        startHeightInMeters = startHeight.toDouble(),
        startAngleInDegrees = startAngle.toDouble(),
        startAngleInRads = startAngle.toDouble().degreesToRads(),
        finishHeightInMeters = finishHeight.toDouble(),
        finishAngleInDegrees = startAngle.toDouble(),
        finishAngleInRads = finishAngle.toDouble().degreesToRads(),
        speedInMPS = startSpeed.toDouble().kmHtoSI()
    )

internal val InputParameters.v0: Double
    get() = if (!isDrop) {
        sqrt(speedInMPS * speedInMPS - 2 * g * startHeightInMeters)
    } else {
        speedInMPS
    }

internal val InputParameters.v0x: Double
    get() = v0 * cos(startAngleInRads)

internal val InputParameters.v0y: Double
    get() = v0 * sin(startAngleInRads)

internal val InputParameters.hR: Double
    get() = (speedInMPS * speedInMPS) / (2 * g)

internal val InputParameters.startRadiusMin: Double
    get() = (speedInMPS * speedInMPS) / (2 * g)

internal val InputParameters.startRadius: Double
    get() = startHeightInMeters / (1 - cos(startAngleInRads))

internal val InputParameters.startLength: Double
    get() = startRadius * sin(startAngleInRads)

internal val InputParameters.startLengthMin: Double
    get() = startRadiusMin * sin(startAngleInRads)

internal val InputParameters.finishLength: Double
    get() = if (finishHeightInMeters != 0.0) abs(finishHeightInMeters / tan(finishAngleInRads))
    else abs(finishMinY / tan(finishAngleInRads))

internal val InputParameters.finishMinY: Double
    get() = when {
        finishHeightInMeters < 0.0 -> -(ceil(abs(finishHeightInMeters)) - 2)
        else -> -1.0
    }

internal val InputParameters.isDrop: Boolean
    get() = startAngleInRads <= 0

internal val InputParameters.isNotDrop: Boolean
    get() = !isDrop

internal fun calculateLandingParams(inParams: InputParameters): LandingParams {
    val v0x = inParams.v0x
    val v0y = inParams.v0y
    val finishHeight = inParams.finishHeightInMeters
    val finishLength = inParams.finishLength
    val startHeight = inParams.startHeightInMeters
    val gapLength = inParams.gapLengthInMeters
    val finishAngle = inParams.finishAngleInRads

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

internal fun calculateOutParams(inParams: InputParameters, landingParams: LandingParams): OutputParameters {
    val v0 = inParams.v0
    val v0x = inParams.v0x
    val v0y = inParams.v0y
    val hR = inParams.hR

    val startRadiusMin = inParams.startRadiusMin
    val startRadius = inParams.startRadius

    return OutputParameters(
        v0 = v0,
        v0x = v0x,
        v0y = v0y,
        hR = hR,
        startRadius = startRadius,
        startRadiusMin = startRadiusMin,
        hToStart = hR,
        finishLength = inParams.finishLength,
        gLanding = landingParams.gLanding,
    )
}

internal fun createWarningsIfNeed(
    inParams: InputParameters,
    landingParams: LandingParams
): List<CalculationWarnings> {
    val resultList = mutableListOf<CalculationWarnings>()

    if (inParams.gapLengthInMeters > BIG_GAP) resultList.add(CalculationWarnings.BIG_GAP)
    if (landingParams.landingPoint < inParams.gapLengthInMeters) resultList.add(CalculationWarnings.EARLY_LANDING)
    if (landingParams.gLanding > HARD_LANDING_G) resultList.add(CalculationWarnings.HARD_LANDING)

    return resultList
}

internal fun findRoot(diskriminant: Double, a: Double, b: Double): Double = (-b - sqrt(diskriminant)) / (2 * a)

internal fun Double.kmHtoSI(): Double = this * METERS_IN_KILOMETER / SECONDS_IN_HOUR

internal fun Double.mpStoKmH(): Double = this * SECONDS_IN_HOUR / METERS_IN_KILOMETER

internal fun Double.degreesToRads(): Double = this * PI / HALF_CIRCLE_DEGREES