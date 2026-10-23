package com.github.raininforest.data.entity

import com.github.raininforest.data.ELEMENTS_BLUE
import com.github.raininforest.data.MIN_R_FILL
import com.github.raininforest.data.MIN_R_LINE
import com.github.raininforest.data.RAMP_FILL
import com.github.raininforest.data.TOLERANCE_OLIVE
import com.github.raininforest.data.TRACE_GREEN

/** Line style of a chart; [fillHexColor] fills the area between the line and y = 0. */
enum class ChartConfig(
    val hexColor: String,
    val thicknessPx: Int,
    val fillHexColor: String? = null
) {
    RAMP(hexColor = ELEMENTS_BLUE, thicknessPx = 4, fillHexColor = RAMP_FILL),
    MIN_R_CHART(hexColor = MIN_R_LINE, thicknessPx = 1, fillHexColor = MIN_R_FILL),
    MAIN_GAP_ELEMENTS(hexColor = ELEMENTS_BLUE, thicknessPx = 4),
    TOLERANCE_TRACE(hexColor = TOLERANCE_OLIVE, thicknessPx = 1),
    MAIN_TRACE(hexColor = TRACE_GREEN, thicknessPx = 3)
}
