package com.github.raininforest.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.raininforest.data.entity.GapParametersEntity
import com.github.raininforest.data.repository.GapEditRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GapEditViewModel(private val gapEditRepository: GapEditRepository) : ViewModel() {

    private companion object {
        const val DEFAULT_ZERO_VALUE = "0.0"
    }

    private var cachedGapId: Long? = null

    private val _gapTitleState = MutableStateFlow<String>("")
    val gapTitleState: StateFlow<String>
        get() = _gapTitleState

    private val fields = GapEditField.entries.associateWith { MutableStateFlow("") }

    fun fieldState(field: GapEditField): StateFlow<String> = fields.getValue(field)

    /** Keeps only what can be part of a number: digits, one decimal separator and a leading minus. */
    fun onFieldChanged(field: GapEditField, text: String) {
        fields.getValue(field).value = text.sanitizeNumber()
    }

    /** Lets a platform apply the same filter to what it shows before the view-model confirms it. */
    fun sanitized(text: String): String = text.sanitizeNumber()

    /** True when the field holds a value that will be used as typed (empty counts as 0). */
    fun isFieldValid(text: String): Boolean =
        text.isEmpty() || text.replace(',', '.').toDoubleOrNull() != null
    private var fetchJob: Job? = null

    fun fetchGapParameters(gapId: Long) {
        cachedGapId = gapId
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            val editParametersUi = gapEditRepository.gapEditParameters(gapId)
            val gapTitle = gapEditRepository.gapTitle(gapId)
            if (editParametersUi != null) {
                onFieldChanged(GapEditField.GAP, editParametersUi.gap)
                onFieldChanged(GapEditField.TABLE, editParametersUi.table)
                onFieldChanged(GapEditField.START_HEIGHT, editParametersUi.startHeight)
                onFieldChanged(GapEditField.START_ANGLE, editParametersUi.startAngle)
                onFieldChanged(GapEditField.FINISH_HEIGHT, editParametersUi.finishHeight)
                onFieldChanged(GapEditField.FINISH_ANGLE, editParametersUi.finishAngle)
                onFieldChanged(GapEditField.START_SPEED, editParametersUi.startSpeed)
            }
            _gapTitleState.value = gapTitle
        }
    }

    fun gapTitleChanged(text: String) {
        _gapTitleState.value = text
    }

    /** Saves the edited values; [onSaved] runs after the write finished, so the screen can be closed safely. */
    fun onApplyClicked(onSaved: () -> Unit) {
        val gapId = cachedGapId ?: return
        val title = _gapTitleState.value
        val parameters = getParams()
        viewModelScope.launch {
            gapEditRepository.changeGapTitle(gapId = gapId, title = title)
            gapEditRepository.changeGapParameters(gapId = gapId, parameters = parameters)
            onSaved()
        }
    }

    private fun getParams() = GapParametersEntity(
        gap = value(GapEditField.GAP).validateInput(),
        table = value(GapEditField.TABLE),
        startHeight = value(GapEditField.START_HEIGHT),
        startAngle = value(GapEditField.START_ANGLE),
        finishHeight = value(GapEditField.FINISH_HEIGHT),
        finishAngle = value(GapEditField.FINISH_ANGLE),
        startSpeed = value(GapEditField.START_SPEED)
    )

    private fun value(field: GapEditField) = fields.getValue(field).value

    private fun String.validateInput(): String {
        val safeVal = this
            .replace(oldChar = ',', newChar = '.')
            .ifEmpty { DEFAULT_ZERO_VALUE }
        return if (safeVal.toDoubleOrNull() != null) safeVal else DEFAULT_ZERO_VALUE
    }

    private fun String.sanitizeNumber(): String {
        var separatorSeen = false
        return filterIndexed { index, char ->
            when {
                char.isDigit() -> true
                char == '-' -> index == 0
                char == '.' || char == ',' -> (!separatorSeen).also { separatorSeen = true }
                else -> false
            }
        }
    }
}
