package com.github.raininforest.android.ui.gap.common

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.github.raininforest.android.R
import com.github.raininforest.data.entity.CalculationWarnings
import com.github.raininforest.data.entity.GapParameter
import com.github.raininforest.data.entity.ParameterUnit
import com.github.raininforest.data.entity.ParameterValue
import com.github.raininforest.data.entity.TextData

@StringRes
fun GapParameter.labelRes(): Int = when (this) {
    GapParameter.GAP -> R.string.param_gap
    GapParameter.TABLE -> R.string.param_table
    GapParameter.START_HEIGHT -> R.string.param_start_height
    GapParameter.START_ANGLE -> R.string.param_start_angle
    GapParameter.FINISH_HEIGHT -> R.string.param_finish_height
    GapParameter.FINISH_ANGLE -> R.string.param_finish_angle
    GapParameter.START_SPEED -> R.string.param_start_speed
    GapParameter.SPEED_0 -> R.string.param_speed_0
    GapParameter.R_START_MIN -> R.string.param_r_start_min
    GapParameter.R_START -> R.string.param_r_start
    GapParameter.H_WHERE_START -> R.string.param_h_where_start
    GapParameter.G_LANDING -> R.string.param_g_landing
}

@StringRes
fun ParameterUnit.symbolRes(): Int = when (this) {
    ParameterUnit.METER -> R.string.unit_meter
    ParameterUnit.DEGREE -> R.string.unit_degree
    ParameterUnit.KMH -> R.string.unit_kmh
}

@StringRes
fun CalculationWarnings.textRes(): Int = when (this) {
    CalculationWarnings.HARD_LANDING -> R.string.warning_hard_landing
    CalculationWarnings.EARLY_LANDING -> R.string.warning_early_landing
    CalculationWarnings.BIG_GAP -> R.string.warning_big_gap
}

/** Hint drawing shown next to an input field. */
@DrawableRes
fun GapParameter.iconRes(): Int? = when (this) {
    GapParameter.GAP -> R.drawable.ic_gap
    GapParameter.TABLE -> R.drawable.ic_table
    GapParameter.START_HEIGHT -> R.drawable.ic_startheight
    GapParameter.START_ANGLE -> R.drawable.ic_startangle
    GapParameter.FINISH_HEIGHT -> R.drawable.ic_finishheight
    GapParameter.FINISH_ANGLE -> R.drawable.ic_finishangle
    GapParameter.START_SPEED -> R.drawable.ic_speed
    else -> null
}

/** "Takeoff height, m" */
fun Context.inputLabel(parameter: GapParameter): String =
    "${getString(parameter.labelRes())}, ${getString(parameter.unit.symbolRes())}"

/** "Gap = 2 m" */
fun Context.parameterLine(item: ParameterValue): String =
    "${getString(item.parameter.labelRes())} = ${item.value} ${getString(item.parameter.unit.symbolRes())}"

fun Context.parameterLines(items: List<ParameterValue>): String = items.joinToString("\n") { parameterLine(it) }

/** The text that is handed to the share sheet. */
fun Context.shareText(textData: TextData): String =
    parameterLines(textData.inputGapParameters + textData.outputGapParameters)
