package com.github.raininforest.di

import com.github.raininforest.usecases.calculator.Calculator
import com.github.raininforest.usecases.calculator.CalculatorImpl
import com.github.raininforest.data.repository.GapDetailsRepository
import com.github.raininforest.data.repository.GapEditRepository
import com.github.raininforest.data.repository.GapListRepository
import com.github.raininforest.usecases.parametercomposer.ParameterComposer
import com.github.raininforest.db.AsyncLazy
import com.github.raininforest.db.DBFactory
import com.github.raininforest.db.DBSource
import com.github.raininforest.db.DBSourceImpl
import com.github.raininforest.db.ioDispatcher
import com.github.raininforest.usecases.share.ShareService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class Dependencies(
    private val platformDependencies: PlatformDependencies,
    private val ioDispatcher: CoroutineDispatcher,
    private val defaultDispatcher: CoroutineDispatcher
) {
    // secondary constructor instead of default arguments: those aren't visible from Swift
    constructor(platformDependencies: PlatformDependencies) :
        this(platformDependencies, ioDispatcher, Dispatchers.Default)

    val gapListRepository: GapListRepository by lazy { GapListRepository(dbSource) }

    val gapDetailsRepository: GapDetailsRepository by lazy {
        GapDetailsRepository(
            dbSource = dbSource,
            calculator = calculator,
            parameterComposer = parameterComposer
        )
    }

    val gapEditRepository: GapEditRepository by lazy { GapEditRepository(dbSource) }

    private val calculator: Calculator by lazy { CalculatorImpl(defaultDispatcher) }

    private val parameterComposer: ParameterComposer by lazy { ParameterComposer() }

    private val dbSource: DBSource by lazy {
        DBSourceImpl(AsyncLazy { dbFactory.createDB() }, ioDispatcher)
    }

    private val dbFactory: DBFactory by lazy { DBFactory(platformDependencies.sqlDriverFactory) }

    val shareService: ShareService by lazy { platformDependencies.shareService }
}
