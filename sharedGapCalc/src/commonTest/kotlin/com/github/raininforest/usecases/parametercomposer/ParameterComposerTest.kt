package com.github.raininforest.usecases.parametercomposer

import com.github.raininforest.data.entity.GapParameter
import com.github.raininforest.data.entity.GapParametersEntity
import com.github.raininforest.data.entity.OutputParameters
import com.github.raininforest.data.entity.ParameterValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ParameterComposerTest {

    @Test
    fun composesInputAsEnteredAndOutputTruncatedToTwoDecimals() {
        val text = ParameterComposer().composeParameters(
            inputParameters = GapParametersEntity("2", "2", "1.2", "30", "0.8", "18", "27.5"),
            outputParameters = OutputParameters(
                v0 = 10.0, v0x = 0.0, v0y = 0.0,
                startRadiusMin = 5.12345, startRadius = 6.0, hToStart = 1.999, hR = 0.0,
                finishLength = 0.0, gLanding = 0.5
            )
        )

        assertEquals(ParameterValue(GapParameter.GAP, "2"), text.inputGapParameters.first())
        assertEquals(7, text.inputGapParameters.size)
        assertEquals(
            listOf(
                ParameterValue(GapParameter.SPEED_0, "36.0"),
                ParameterValue(GapParameter.R_START_MIN, "5.12"),
                ParameterValue(GapParameter.R_START, "6.0"),
                ParameterValue(GapParameter.H_WHERE_START, "1.99"),
                ParameterValue(GapParameter.G_LANDING, "0.5")
            ),
            text.outputGapParameters
        )
    }

    @Test
    fun truncate_cutsWithoutRounding() {
        assertEquals("1.99", 1.999.truncate(2))
        assertEquals("0.5", 0.5.truncate(2))
        assertEquals("36.0", 36.0.truncate(2))
        assertEquals("2.97", 2.9789.truncate(2))
        assertEquals("0.0", 0.004.truncate(2))
        assertEquals("-1.5", (-1.5).truncate(2))
        assertEquals("0.0", (-0.001).truncate(2))
    }
}
