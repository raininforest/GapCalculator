package com.github.raininforest.ui.edit.data

sealed class GapEditState {
    data class GapEditData(
        val gap: String,
        val table: String,
        val startHeight: String,
        val startAngle: String,
        val finishHeight: String,
        val finishAngle: String,
        val startSpeed: String
    ) : GapEditState()

    data object GapEditEmpty : GapEditState()
}

