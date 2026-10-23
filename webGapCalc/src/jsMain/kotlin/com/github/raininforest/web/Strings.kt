package com.github.raininforest.web

import com.github.raininforest.data.entity.CalculationWarnings
import com.github.raininforest.data.entity.GapParameter
import com.github.raininforest.data.entity.ParameterUnit
import com.github.raininforest.data.entity.ParameterValue
import com.github.raininforest.data.entity.TextData
import kotlinx.browser.window
import org.w3c.dom.url.URLSearchParams

/** UI texts; English by default, Russian when the browser language is Russian. */
object Strings {
    // `?lang=en` / `?lang=ru` overrides the browser language
    private val language = URLSearchParams(window.location.search).get("lang") ?: window.navigator.language
    private val russian = language.startsWith("ru")

    private fun tr(en: String, ru: String) = if (russian) ru else en

    val title get() = "Gap Calculator"
    val add get() = tr("Add", "Добавить")
    val edit get() = tr("Edit", "Редактировать")
    val apply get() = tr("Apply", "Применить")
    val empty get() = tr("Empty", "Пусто")
    val untitled get() = tr("Untitled", "Без названия")
    val back get() = tr("Back", "Назад")
    val share get() = tr("Share", "Поделиться")
    val copied get() = tr("Copied to clipboard", "Скопировано в буфер обмена")

    fun label(parameter: GapParameter) = when (parameter) {
        GapParameter.GAP -> tr("Gap", "Гэп")
        GapParameter.TABLE -> tr("Table", "Стол")
        GapParameter.START_HEIGHT -> tr("Takeoff height", "Высота вылета")
        GapParameter.START_ANGLE -> tr("Takeoff angle", "Угол вылета")
        GapParameter.FINISH_HEIGHT -> tr("Landing height", "Высота приземления")
        GapParameter.FINISH_ANGLE -> tr("Landing angle", "Угол приземления")
        GapParameter.START_SPEED -> tr("Run-up speed", "Скорость разгона")
        GapParameter.SPEED_0 -> "V0"
        GapParameter.R_START_MIN -> tr("Takeoff R min", "R вылета min")
        GapParameter.R_START -> tr("Takeoff R", "R вылета")
        GapParameter.H_WHERE_START -> tr("Run-up height", "Высота разгона")
        GapParameter.G_LANDING -> tr("Landing G", "G приземления")
    }

    fun unit(unit: ParameterUnit) = when (unit) {
        ParameterUnit.METER -> tr("m", "м")
        ParameterUnit.DEGREE -> tr("deg.", "град.")
        ParameterUnit.KMH -> tr("km/h", "км/ч")
    }

    fun warning(warning: CalculationWarnings) = when (warning) {
        CalculationWarnings.HARD_LANDING -> tr("The landing will be hard", "Приземление будет жестким")
        CalculationWarnings.EARLY_LANDING -> tr("Looks like an undershoot", "Похоже, недолёт")
        CalculationWarnings.BIG_GAP -> tr("Big jump", "Большой трамплин")
    }

    fun inputLabel(parameter: GapParameter) = "${label(parameter)}, ${unit(parameter.unit)}"

    fun line(item: ParameterValue) = "${label(item.parameter)} = ${item.value} ${unit(item.parameter.unit)}"

    fun lines(items: List<ParameterValue>) = items.joinToString("\n") { line(it) }

    fun shareText(textData: TextData) = lines(textData.inputGapParameters + textData.outputGapParameters)
}

fun GapParameter.iconFile(): String? = when (this) {
    GapParameter.GAP -> "gap"
    GapParameter.TABLE -> "table"
    GapParameter.START_HEIGHT -> "startheight"
    GapParameter.START_ANGLE -> "startangle"
    GapParameter.FINISH_HEIGHT -> "finishheight"
    GapParameter.FINISH_ANGLE -> "finishangle"
    GapParameter.START_SPEED -> "speed"
    else -> null
}
