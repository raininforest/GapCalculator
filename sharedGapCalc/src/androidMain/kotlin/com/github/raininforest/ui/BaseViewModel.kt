package com.github.raininforest.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope

actual open class BaseViewModel : ViewModel() {
    protected actual val coroutineScope: CoroutineScope
        get() = viewModelScope

    actual open fun clear() = Unit
}
