package com.github.raininforest.usecases.parametercomposer

import kotlin.math.abs
import kotlin.math.pow

/**
 * Cuts [this] off (no rounding) after [decimals] digits, always keeping at least one: 1.999 -> "1.99", 36.0 -> "36.0".
 * Written by hand because `Double.toString()` differs between platforms (js prints 36 for 36.0).
 */
internal fun Double.truncate(decimals: Int): String {
    if (isNaN() || isInfinite()) return toString()
    val factor = 10.0.pow(decimals)
    val scaled = (abs(this) * factor).toLong()
    val integerPart = scaled / factor.toLong()
    val fraction = (scaled % factor.toLong()).toString().padStart(decimals, '0').trimEnd('0').ifEmpty { "0" }
    val sign = if (this < 0 && scaled != 0L) "-" else ""
    return "$sign$integerPart.$fraction"
}
