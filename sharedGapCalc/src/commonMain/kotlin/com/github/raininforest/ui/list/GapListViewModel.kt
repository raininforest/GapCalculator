package com.github.raininforest.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.raininforest.data.entity.GapListItemEntity
import com.github.raininforest.data.repository.GapListRepository
import com.github.raininforest.ui.list.data.GapListItem
import com.github.raininforest.ui.list.data.GapListState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GapListViewModel(
    private val gapListRepository: GapListRepository
) : ViewModel() {

    private val _gapListState = MutableStateFlow<GapListState>(GapListState.GapListEmpty)
    val gapList: StateFlow<GapListState>
        get() = _gapListState

    private var fetchJob: Job? = null

    init {
        fetchList()
    }

    fun removeGapClicked(id: Long) {
        viewModelScope.launch {
            gapListRepository.removeGap(id)
            fetchList()
        }
    }

    fun addClicked() {
        viewModelScope.launch {
            gapListRepository.addGap()
            fetchList()
        }
    }

    /** Reloads the list; a newer request cancels the one still in flight so stale results can't overwrite it. */
    fun fetchList() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            _gapListState.value = gapListRepository.gapList().toUiState()
        }
    }

    private fun List<GapListItemEntity>.toUiState(): GapListState =
        if (isNotEmpty()) {
            GapListState.GapListData(
                gapList = map { GapListItem(id = it.id, title = it.title, date = it.date) }
            )
        } else {
            GapListState.GapListEmpty
        }
}
