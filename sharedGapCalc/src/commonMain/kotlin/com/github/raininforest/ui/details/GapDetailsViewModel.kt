package com.github.raininforest.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.raininforest.data.repository.GapDetailsRepository
import com.github.raininforest.data.entity.GapDetailsEntity
import com.github.raininforest.ui.details.data.GapDetailsState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GapDetailsViewModel(
    private val gapDetailsRepository: GapDetailsRepository
) : ViewModel() {
    private val _gapDetailsState = MutableStateFlow<GapDetailsState>(GapDetailsState.Loading)
    val gapDetails: StateFlow<GapDetailsState>
        get() = _gapDetailsState

    private var fetchJob: Job? = null

    fun fetchGapDetails(gapId: Long) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            _gapDetailsState.value = gapDetailsRepository.getGapDetails(gapId).toUiState()
        }
    }

    private fun GapDetailsEntity.toUiState(): GapDetailsState =
        if (chartData.charts.isEmpty()) {
            GapDetailsState.GapDetailsEmpty
        } else {
            GapDetailsState.GapDetailsData(
                gapTitle = gapTitle,
                chartData = chartData,
                textData = textData,
                warnings = warnings
            )
        }
}
