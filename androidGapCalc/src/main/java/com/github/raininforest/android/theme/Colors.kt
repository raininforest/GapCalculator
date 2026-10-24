package com.github.raininforest.android.theme

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.ui.graphics.Color

val whiteGray = Color(0xFFFFFFFF)
val lightGray = Color(0xFFDFDFDF)
val darkGray = Color(0xFF272727)
val darkestGray = Color(0xFF1B1B1B)

// white text on this green has a 5.1:1 contrast (the former #4CAF50 had 2.8:1)
val green = Color(0xFF2E7D32)
val red = Color(0xFFF44336)
val orange = Color(0xFFFB8C00)
val amber = Color(0xFFFFB300)

/** Content never gets wider than this, so phones in landscape and tablets don't stretch the screens. */
const val MAX_CONTENT_WIDTH_DP = 640

val customTextSelectionColors = TextSelectionColors(
    handleColor = Color.Transparent,
    backgroundColor = Color.Transparent,
)
