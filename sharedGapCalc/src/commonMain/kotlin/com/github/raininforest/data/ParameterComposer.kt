package com.github.raininforest.data

import com.github.raininforest.data.entity.GapParametersEntity
import com.github.raininforest.data.entity.OutputParameters
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
                "$SPEED_0 = ${outputParameters.v0} $KMH",
                "$R_START_MIN = ${outputParameters.rStartMin} $METER",
                "$R_START = ${outputParameters.rStart} $METER",
                "$H_WHERE_START = ${outputParameters.hToStart} $METER",
                "$G_LANDING = ${outputParameters.gLanding} $METER",
            )
        )
    }
}