package com.github.raininforest.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Swift-friendly collector for [StateFlow]: Kotlin flows can't be consumed from Swift directly.
 * Calls [onChange] on the main thread with the current value and every following one until [cancel].
 */
class FlowWatcher<T : Any>(flow: StateFlow<T>, private val onChange: (T) -> Unit) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    init {
        scope.launch {
            flow.collect { onChange(it) }
        }
    }

    fun cancel() = scope.cancel()
}
