package com.github.raininforest.ui.details

import com.github.raininforest.data.repository.GapDetailsRepository
import com.github.raininforest.data.entity.CalculationWarnings
import com.github.raininforest.data.entity.GapDetailsEntity
import com.github.raininforest.usecases.share.ShareService
import com.github.raininforest.ui.BaseViewModel
import com.github.raininforest.ui.details.data.GapDetailsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GapDetailsViewModel(
    private val gapDetailsRepository: GapDetailsRepository,
    private val shareService: ShareService
) : BaseViewModel() {
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

    fun onShareClicked() {
        val currentParameters = _gapDetailsState.value
        if (currentParameters is GapDetailsState.GapDetailsData) {
            val textData = currentParameters.textData
            val finalList = textData.inputGapParameters + textData.outputGapParameters
            shareService.share(finalList.joinToString("\n"))
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
                warnings = warnings.toWarningText()
            )
        }

    private fun List<CalculationWarnings>.toWarningText(): List<String> {
        return this.map { warning ->
            when (warning) {
                CalculationWarnings.HARD_LANDING -> "Приземление будет жестким"
                CalculationWarnings.EARLY_LANDING -> "Похоже, недолёт"
                CalculationWarnings.BIG_GAP -> "Большой трамплин"
            }
        }
    }
}
