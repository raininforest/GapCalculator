package com.github.raininforest.data.entity

import com.github.raininforest.data.BLUE
import com.github.raininforest.data.GREEN
import com.github.raininforest.data.RED

enum class ChartConfig(
    val hexColor: String,
    val thicknessPx: Int
) {
    MIN_R_CHART(hexColor = RED, thicknessPx = 1),
    MAIN_GAP_ELEMENTS(hexColor = BLUE, thicknessPx = 3),
    MAIN_TRACE(hexColor = GREEN, thicknessPx = 3)
}
