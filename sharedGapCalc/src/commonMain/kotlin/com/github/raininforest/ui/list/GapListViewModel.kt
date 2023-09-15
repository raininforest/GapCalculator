package com.github.raininforest.ui.list

import com.github.raininforest.data.GapListRepository
import com.github.raininforest.data.entity.GapListItemEntity
import com.github.raininforest.ui.BaseViewModel
import com.github.raininforest.ui.list.data.GapListItem
import com.github.raininforest.ui.list.data.GapListState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GapListViewModel(
    private val gapListRepository: GapListRepository
) : BaseViewModel() {

    private val _gapListState = MutableStateFlow<GapListState>(GapListState.GapListEmpty)
    val gapList: StateFlow<GapListState>
        get() = _gapListState

    init {
        coroutineScope.launch(Dispatchers.IO) {
            val uiState = gapListRepository.gapList().toUiState()
            withContext(Dispatchers.Main) {
                _gapListState.value = uiState
            }
        }
    }

    private fun List<GapListItemEntity>.toUiState(): GapListState =
        GapListState.GapListData(
            gapList = map {
                GapListItem(
                    id = it.id,
                    title = it.title,
                    date = it.date
                )
            }
        )
}