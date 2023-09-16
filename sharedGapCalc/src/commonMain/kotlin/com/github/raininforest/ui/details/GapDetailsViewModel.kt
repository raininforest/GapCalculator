package com.github.raininforest.ui.details

import com.github.raininforest.data.GapDetailsRepository
import com.github.raininforest.data.entity.GapDetailsEntity
import com.github.raininforest.ui.BaseViewModel
import com.github.raininforest.ui.details.data.GapDetailsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GapDetailsViewModel(private val gapDetailsRepository: GapDetailsRepository) : BaseViewModel() {
    private val _gapDetailsState = MutableStateFlow<GapDetailsState>(GapDetailsState.GapDetailsEmpty)
    val gapDetails: StateFlow<GapDetailsState>
        get() = _gapDetailsState

    fun fetchGapDetails(gapId: Long) {
        coroutineScope.launch(Dispatchers.IO) {
            val gapDetailsUi = gapDetailsRepository.getGapDetails(gapId)
            withContext(Dispatchers.Main) {
                _gapDetailsState.value = gapDetailsUi.toUiState()
            }
        }
    }

    private fun GapDetailsEntity.toUiState(): GapDetailsState =
        if (false) { // todo if (this.chartData.charts.isEmpty())
            GapDetailsState.GapDetailsEmpty
        } else {
            GapDetailsState.GapDetailsData(
                gapTitle = gapTitle,
                chartData = chartData,
                textData = textData
            )
        }
}
