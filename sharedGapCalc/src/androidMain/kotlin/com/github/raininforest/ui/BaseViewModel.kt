package com.github.raininforest.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive

actual open class BaseViewModel : ViewModel() {
    protected actual val coroutineScope: CoroutineScope
        get() = viewModelScope

    actual open fun clear() = if (coroutineScope.isActive) coroutineScope.cancel() else Unit
}
