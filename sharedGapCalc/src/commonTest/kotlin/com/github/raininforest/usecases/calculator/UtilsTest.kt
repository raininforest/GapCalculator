package com.github.raininforest.usecases.calculator

import com.github.raininforest.data.entity.CalculationWarnings
import com.github.raininforest.data.entity.GapParametersEntity
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UtilsTest {

    private val eps = 1e-6

    private fun assertClose(expected: Double, actual: Double, tolerance: Double = eps) {
        assertTrue(abs(expected - actual) <= tolerance, "expected $expected but was $actual")
    }

    private fun params(
        gap: Double = 3.0,
        table: Double = 3.0,
        startHeight: Double = 0.0,
        startAngle: Double = 45.0,
        finishHeight: Double = 0.0,
        finishAngle: Double = 45.0,
        speedKmH: Double = 36.0
    ) = InputParameters(
        gapLengthInMeters = gap,
        tableLengthInMeters = table,
        startHeightInMeters = startHeight,
        startAngleInRads = startAngle.degreesToRads(),
        finishHeightInMeters = finishHeight,
        finishAngleInRads = finishAngle.degreesToRads(),
        speedInMPS = speedKmH.kmHtoSI(),
        startAngleInDegrees = startAngle,
        finishAngleInDegrees = finishAngle
    )

    @Test
    fun unitConversions() {
        assertClose(10.0, 36.0.kmHtoSI())
        assertClose(36.0, 10.0.mpStoKmH())
        assertClose(PI, 180.0.degreesToRads(), tolerance = 1e-9)
        assertClose(0.0, 0.0.degreesToRads())
    }

    @Test
    fun convertToSINumbers_mapsAllFields() {
        val result = GapParametersEntity(
            gap = "2",
            table = "3",
            startHeight = "1.2",
            startAngle = "30",
            finishHeight = "0.8",
            finishAngle = "18",
            startSpeed = "36"
        ).convertToSINumbers()

        assertEquals(2.0, result.gapLengthInMeters)
        assertEquals(3.0, result.tableLengthInMeters)
        assertEquals(1.2, result.startHeightInMeters)
        assertEquals(30.0, result.startAngleInDegrees)
        assertEquals(0.8, result.finishHeightInMeters)
        assertEquals(18.0, result.finishAngleInDegrees)
        assertClose(18.0.degreesToRads(), result.finishAngleInRads, tolerance = 1e-9)
        assertClose(10.0, result.speedInMPS)
    }

    @Test
    fun velocityComponents_atFortyFiveDegrees() {
        val p = params(startAngle = 45.0, startHeight = 0.0, speedKmH = 36.0)

        assertClose(10.0, p.v0)
        assertClose(10.0 * sin(PI / 4), p.v0x, tolerance = 1e-6)
        assertClose(p.v0x, p.v0y, tolerance = 1e-6)
    }

    @Test
    fun v0_lossesEnergyToTakeoffHeight() {
        val p = params(startHeight = 1.0, speedKmH = 36.0)

        assertClose(kotlin.math.sqrt(100.0 - 2 * g * 1.0), p.v0)
    }

    @Test
    fun isDrop_whenAngleIsZeroOrNegative() {
        assertTrue(params(startAngle = 0.0).isDrop)
        assertTrue(params(startAngle = -10.0).isDrop)
        assertTrue(params(startAngle = 10.0).isNotDrop)
    }

    @Test
    fun v0_forDropIsPlainSpeed() {
        val p = params(startAngle = -10.0, startHeight = 1.0, speedKmH = 36.0)

        assertClose(10.0, p.v0)
    }

    @Test
    fun flightFx_isParabola() {
        assertClose(0.0, flightFx(x = 0.0, v0x = 10.0, v0y = 10.0))
        // t = 1s -> y = v0y - g/2
        assertClose(10.0 - g / 2, flightFx(x = 10.0, v0x = 10.0, v0y = 10.0))
        // symmetric: returns to zero at x = 2 * v0x * v0y / g
        assertClose(0.0, flightFx(x = 2 * 10.0 * 10.0 / g, v0x = 10.0, v0y = 10.0), tolerance = 1e-9)
    }

    @Test
    fun flightFx_zeroHorizontalVelocityIsZero() {
        assertEquals(0.0, flightFx(x = 5.0, v0x = 0.0, v0y = 10.0))
    }

    @Test
    fun startCircleFx_isZeroAtLipAndRisesToRadiusHeightAtStart() {
        val radius = 5.0
        val xRange = 3.0
        // x = 0 is the lip: -sqrt(r^2 - xRange^2) + r
        assertClose(radius - kotlin.math.sqrt(radius * radius - xRange * xRange), startCircleFx(0.0, radius, xRange))
        // x = -xRange is the bottom of the circle
        assertClose(0.0, startCircleFx(-xRange, radius, xRange))
    }

    @Test
    fun landingParams_flatFinishAtFortyFiveDegrees() {
        val landing = calculateLandingParams(params(finishAngle = 0.0))

        // range of a projectile: v^2 * sin(2a) / g
        assertClose(100.0 / g, landing.landingPoint, tolerance = 1e-4)
        // lands at 45deg with v = 10 on flat ground: (v*sin45)^2 / 2g
        assertClose(50.0 / (2 * g), landing.gLanding, tolerance = 1e-4)
    }

    @Test
    fun landingParams_matchingSlopeGivesSoftLanding() {
        val landing = calculateLandingParams(params(finishAngle = 45.0))

        assertClose(100.0 / g, landing.landingPoint, tolerance = 1e-4)
        assertClose(0.0, landing.gLanding, tolerance = 1e-4)
    }

    @Test
    fun landingParams_unreachableLandingReturnsZeros() {
        // 1 km/h can't reach a landing 5 m higher
        val landing =
            calculateLandingParams(params(finishHeight = 5.0, finishAngle = 30.0, speedKmH = 1.0, startAngle = 10.0))

        assertEquals(0.0, landing.landingPoint)
        assertEquals(0.0, landing.gLanding)
    }

    @Test
    fun warnings_none() {
        val warnings = createWarningsIfNeed(
            inParams = params(gap = 3.0),
            landingParams = LandingParams(landingPoint = 5.0, gLanding = 0.5)
        )

        assertEquals(emptyList(), warnings)
    }

    @Test
    fun warnings_earlyLandingWhenLandingPointBeforeFinish() {
        val warnings = createWarningsIfNeed(
            inParams = params(gap = 3.0),
            landingParams = LandingParams(landingPoint = 2.9, gLanding = 0.5)
        )

        assertEquals(listOf(CalculationWarnings.EARLY_LANDING), warnings)
    }

    @Test
    fun warnings_hardLandingAndBigGap() {
        val warnings = createWarningsIfNeed(
            inParams = params(gap = 4.5),
            landingParams = LandingParams(landingPoint = 10.0, gLanding = 1.5)
        )

        assertEquals(listOf(CalculationWarnings.BIG_GAP, CalculationWarnings.HARD_LANDING), warnings)
    }

    @Test
    fun warnings_boundariesAreExclusive() {
        val warnings = createWarningsIfNeed(
            inParams = params(gap = 4.0),
            landingParams = LandingParams(landingPoint = 4.0, gLanding = 1.0)
        )

        assertEquals(emptyList(), warnings)
    }
}
