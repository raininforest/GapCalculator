package com.github.raininforest.db

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** `lazy` for a suspending initializer: runs it once, concurrent callers wait for the same result. */
class AsyncLazy<T : Any>(private val initializer: suspend () -> T) {
    private val mutex = Mutex()
    private var value: T? = null

    suspend fun get(): T = value ?: mutex.withLock { value ?: initializer().also { value = it } }
}
