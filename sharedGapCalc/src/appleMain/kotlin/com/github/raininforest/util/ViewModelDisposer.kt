package com.github.raininforest.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel

/**
 * iOS has no ViewModelStoreOwner, so Swift owns the view-model and calls this when its screen goes away:
 * cancels everything running in `viewModelScope`.
 */
fun ViewModel.dispose() = viewModelScope.cancel()
