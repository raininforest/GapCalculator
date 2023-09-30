package com.github.raininforest.data.entity

import com.github.raininforest.data.BROWN
import com.github.raininforest.data.DARK_GRAY
import com.github.raininforest.data.SEMI_TRANSPARENT_RED

enum class ChartConfig(
    val hexColor: String,
    val thicknessPx: Int,
) {
    MIN_R_CHART(hexColor = SEMI_TRANSPARENT_RED, thicknessPx = 1),
    MAIN_GAP_ELEMENTS(hexColor = BROWN, thicknessPx = 3),
    MAIN_TRACE(hexColor = DARK_GRAY, thicknessPx = 1)
}
