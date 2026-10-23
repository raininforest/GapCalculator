package com.github.raininforest.usecases.parametercomposer

import com.github.raininforest.data.entity.GapParameter
import com.github.raininforest.data.entity.GapParametersEntity
import com.github.raininforest.data.entity.OutputParameters
import com.github.raininforest.data.entity.ParameterValue
import com.github.raininforest.data.entity.TextData
import com.github.raininforest.usecases.calculator.mpStoKmH

class ParameterComposer {
    fun composeParameters(inputParameters: GapParametersEntity, outputParameters: OutputParameters): TextData {
        return TextData(
            inputGapParameters = listOf(
                ParameterValue(GapParameter.GAP, inputParameters.gap),
                ParameterValue(GapParameter.TABLE, inputParameters.table),
                ParameterValue(GapParameter.START_ANGLE, inputParameters.startAngle),
                ParameterValue(GapParameter.START_HEIGHT, inputParameters.startHeight),
                ParameterValue(GapParameter.FINISH_ANGLE, inputParameters.finishAngle),
                ParameterValue(GapParameter.FINISH_HEIGHT, inputParameters.finishHeight),
                ParameterValue(GapParameter.START_SPEED, inputParameters.startSpeed),
            ),
            outputGapParameters = listOf(
                ParameterValue(GapParameter.SPEED_0, outputParameters.v0.mpStoKmH().truncate(DECIMALS)),
                ParameterValue(GapParameter.R_START_MIN, outputParameters.startRadiusMin.truncate(DECIMALS)),
                ParameterValue(GapParameter.R_START, outputParameters.startRadius.truncate(DECIMALS)),
                ParameterValue(GapParameter.H_WHERE_START, outputParameters.hToStart.truncate(DECIMALS)),
                ParameterValue(GapParameter.G_LANDING, outputParameters.gLanding.truncate(DECIMALS)),
            )
        )
    }

    private companion object {
        const val DECIMALS = 2
    }
}
