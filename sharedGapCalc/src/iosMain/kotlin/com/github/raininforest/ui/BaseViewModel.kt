package com.github.raininforest.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive

actual open class BaseViewModel actual constructor() {
    protected actual val coroutineScope: CoroutineScope by lazy {
        CoroutineScope(context = SupervisorJob() + Dispatchers.Main)
    }

    actual open fun clear() = if (coroutineScope.isActive) coroutineScope.cancel() else Unit
}
