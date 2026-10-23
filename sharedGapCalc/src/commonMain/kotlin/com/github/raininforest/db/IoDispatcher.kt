package com.github.raininforest.db

import kotlinx.coroutines.CoroutineDispatcher

/** Dispatcher for blocking database work; kotlinx.coroutines has no IO dispatcher on every target (js). */
expect val ioDispatcher: CoroutineDispatcher
