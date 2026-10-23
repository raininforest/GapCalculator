package com.github.raininforest.data.entity

enum class ParameterUnit { METER, DEGREE, KMH }

/** Every named value shown to the user; the platforms translate the name and the unit. */
enum class GapParameter(val unit: ParameterUnit) {
    GAP(ParameterUnit.METER),
    TABLE(ParameterUnit.METER),
    START_HEIGHT(ParameterUnit.METER),
    START_ANGLE(ParameterUnit.DEGREE),
    FINISH_HEIGHT(ParameterUnit.METER),
    FINISH_ANGLE(ParameterUnit.DEGREE),
    START_SPEED(ParameterUnit.KMH),

    SPEED_0(ParameterUnit.KMH),
    R_START_MIN(ParameterUnit.METER),
    R_START(ParameterUnit.METER),
    H_WHERE_START(ParameterUnit.METER),
    G_LANDING(ParameterUnit.METER)
}

data class ParameterValue(
    val parameter: GapParameter,
    val value: String
)
