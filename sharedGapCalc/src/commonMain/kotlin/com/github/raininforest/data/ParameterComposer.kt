package com.github.raininforest.data

import com.github.raininforest.calculator.mpStoKmH
import com.github.raininforest.data.entity.GapParametersEntity
import com.github.raininforest.calculator.OutputParameters
import com.github.raininforest.data.entity.TextData

class ParameterComposer {
    fun composeParameters(inputParameters: GapParametersEntity, outputParameters: OutputParameters): TextData {
        return TextData(
            inputGapParameters = listOf(
                "$GAP = ${inputParameters.gap} $METER",
                "$TABLE = ${inputParameters.table} $METER",
                "$START_ANGLE = ${inputParameters.startAngle} $DEGREE",
                "$START_HEIGHT = ${inputParameters.startHeight} $METER",
                "$FINISH_ANGLE = ${inputParameters.finishAngle} $DEGREE",
                "$FINISH_HEIGHT = ${inputParameters.finishHeight} $METER",
                "$START_SPEED = ${inputParameters.startSpeed} $KMH",
            ),
            outputGapParameters = listOf(
                "$SPEED_0 = ${outputParameters.v0.mpStoKmH().format(2)} $KMH",
                "$R_START_MIN = ${outputParameters.startRadiusMin.format(2)} $METER",
                "$R_START = ${outputParameters.startRadius.format(2)} $METER",
                "$H_WHERE_START = ${outputParameters.hToStart.format(2)} $METER",
                "$G_LANDING = ${outputParameters.gLanding.format(2)} $METER",
            )
        )
    }

    private fun Double.format(decimals: Int): String {
        val str = this.toString()
        val delimiterIndex = str.indexOf('.')
        return str.dropLast(n = str.length - (delimiterIndex + 1 + decimals))
    }
}