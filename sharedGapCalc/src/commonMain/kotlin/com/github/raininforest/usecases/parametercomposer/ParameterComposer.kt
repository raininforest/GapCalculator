package com.github.raininforest.usecases.parametercomposer

import com.github.raininforest.data.entity.OutputParameters
import com.github.raininforest.usecases.calculator.mpStoKmH
import com.github.raininforest.data.DEGREE
import com.github.raininforest.data.FINISH_ANGLE
import com.github.raininforest.data.FINISH_HEIGHT
import com.github.raininforest.data.GAP
import com.github.raininforest.data.G_LANDING
import com.github.raininforest.data.H_WHERE_START
import com.github.raininforest.data.KMH
import com.github.raininforest.data.METER
import com.github.raininforest.data.R_START
import com.github.raininforest.data.R_START_MIN
import com.github.raininforest.data.SPEED_0
import com.github.raininforest.data.START_ANGLE
import com.github.raininforest.data.START_HEIGHT
import com.github.raininforest.data.START_SPEED
import com.github.raininforest.data.TABLE
import com.github.raininforest.data.entity.GapParametersEntity
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
        val numberToDrop = str.length - (delimiterIndex + 1 + decimals)
        return if (numberToDrop < 0) str else str.dropLast(n = numberToDrop)
    }
}