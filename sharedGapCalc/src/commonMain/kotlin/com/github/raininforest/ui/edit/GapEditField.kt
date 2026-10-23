package com.github.raininforest.ui.edit

import com.github.raininforest.data.entity.GapParameter

enum class GapEditField(val parameter: GapParameter) {
    GAP(GapParameter.GAP),
    TABLE(GapParameter.TABLE),
    START_HEIGHT(GapParameter.START_HEIGHT),
    START_ANGLE(GapParameter.START_ANGLE),
    FINISH_HEIGHT(GapParameter.FINISH_HEIGHT),
    FINISH_ANGLE(GapParameter.FINISH_ANGLE),
    START_SPEED(GapParameter.START_SPEED)
}
