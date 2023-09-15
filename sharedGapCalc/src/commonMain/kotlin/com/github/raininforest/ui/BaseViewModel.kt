package com.github.raininforest.ui

import kotlinx.coroutines.CoroutineScope

expect open class BaseViewModel() {
    protected val coroutineScope: CoroutineScope

    open fun clear()
}
