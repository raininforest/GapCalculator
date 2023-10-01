package com.github.raininforest.ui.edit

import com.github.raininforest.data.entity.GapParametersEntity
import com.github.raininforest.data.repository.GapEditRepository
import com.github.raininforest.ui.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GapEditViewModel(private val gapEditRepository: GapEditRepository) : BaseViewModel() {

    private companion object {
        const val DEFAULT_ZERO_VALUE = "0.0"
    }

    private var cachedGapId: Long? = null

    private val _gapTitleState = MutableStateFlow<String>("")
    val gapTitleState: StateFlow<String>
        get() = _gapTitleState

    var gapState = MutableStateFlow<String>("")
    var tableState = MutableStateFlow<String>("")
    var startHeightState = MutableStateFlow<String>("")
    var startAngleState = MutableStateFlow<String>("")
    var finishHeightState = MutableStateFlow<String>("")
    var finishAngleState = MutableStateFlow<String>("")
    var startSpeedState = MutableStateFlow<String>("")


    fun fetchGapParameters(gapId: Long) {
        cachedGapId = gapId
        coroutineScope.launch(Dispatchers.IO) {
            val editParametersUi = gapEditRepository.gapEditParameters(gapId)
            val gapTitle = gapEditRepository.gapTitle(gapId)
            withContext(Dispatchers.Main) {
                if (editParametersUi != null) {
                    gapState.value = editParametersUi.gap
                    tableState.value = editParametersUi.table
                    startHeightState.value = editParametersUi.startHeight
                    startAngleState.value = editParametersUi.startAngle
                    finishHeightState.value = editParametersUi.finishHeight
                    finishAngleState.value = editParametersUi.finishAngle
                    startSpeedState.value = editParametersUi.startSpeed
                }
                _gapTitleState.value = gapTitle
            }
        }
    }

    fun gapTitleChanged(text: String) {
        _gapTitleState.value = text
    }

    fun onApplyClicked() {
        coroutineScope.launch(Dispatchers.IO) {
            cachedGapId?.let {
                gapEditRepository.changeGapTitle(gapId = it, title = _gapTitleState.value)
                gapEditRepository.changeGapParameters(gapId = it, parameters = getParams())
            }
        }
    }

    private fun getParams() = GapParametersEntity(
        gap = gapState.value.validateInput(),
        table = tableState.value,
        startHeight = startHeightState.value,
        startAngle = startAngleState.value,
        finishHeight = finishHeightState.value,
        finishAngle = finishAngleState.value,
        startSpeed = startSpeedState.value
    )

    private fun String.validateInput(): String {
        val safeVal = this
            .replace(oldChar = ',', newChar = '.')
            .ifEmpty { DEFAULT_ZERO_VALUE }
        return try {
            safeVal.toDouble()
            safeVal
        } catch (t: Throwable) {
            DEFAULT_ZERO_VALUE
        }
    }
}
