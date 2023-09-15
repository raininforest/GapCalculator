package com.github.raininforest.ui.list.data

sealed class GapListState {
    data class GapListData(
        val gapList: List<GapListItem>
    ) : GapListState()

    data object GapListEmpty : GapListState()
}
