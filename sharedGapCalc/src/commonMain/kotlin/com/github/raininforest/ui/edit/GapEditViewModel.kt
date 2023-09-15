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

    fun getEditParametersForGap(gapId: String) {
        coroutineScope.launch(Dispatchers.IO) {
            val uiState = gapEditRepository.gapEditParameters(gapId)
            withContext(Dispatchers.Main) {
                _gapEditState.value = uiState.toUiState()
            }
        }
    }

    private fun GapEditEntity.toUiState() =
        GapEditState.GapEditData(
            gap = gap,
            table = table,
            startHeight = startHeight,
            startAngle = startAngle,
            finishHeight = finishHeight,
            finishAngle = finishAngle,
            startSpeed = startSpeed
        )
}
