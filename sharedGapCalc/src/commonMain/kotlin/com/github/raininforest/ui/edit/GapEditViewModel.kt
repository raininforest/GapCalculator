package com.github.raininforest.ui.edit

import com.github.raininforest.data.GapEditRepository
import com.github.raininforest.data.entity.GapEditEntity
import com.github.raininforest.ui.BaseViewModel
import com.github.raininforest.ui.edit.data.GapEditState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GapEditViewModel(private val gapEditRepository: GapEditRepository) : BaseViewModel() {
    private val _gapEditState = MutableStateFlow<GapEditState>(GapEditState.GapEditEmpty)
    val gapEdit: StateFlow<GapEditState>
        get() = _gapEditState

    fun getEditParametersForGap(gapId: Long) {
        coroutineScope.launch(Dispatchers.IO) {
            val uiState = gapEditRepository.gapEditParameters(gapId)
            val gapTitle = gapEditRepository.gapTitle(gapId)
            withContext(Dispatchers.Main) {
                _gapEditState.value = uiState.toUiState(gapTitle)
            }
        }
    }

    private fun GapEditEntity.toUiState(gapTitle: String) =
        GapEditState.GapEditData(
            gapTitle = gapTitle,
            gap = gap,
            table = table,
            startHeight = startHeight,
            startAngle = startAngle,
            finishHeight = finishHeight,
            finishAngle = finishAngle,
            startSpeed = startSpeed
        )
}
