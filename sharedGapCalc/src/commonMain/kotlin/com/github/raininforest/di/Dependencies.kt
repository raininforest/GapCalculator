package com.github.raininforest.di

import com.github.raininforest.GapCalcDatabase
import com.github.raininforest.usecases.calculator.Calculator
import com.github.raininforest.usecases.calculator.CalculatorImpl
import com.github.raininforest.data.repository.GapDetailsRepository
import com.github.raininforest.data.repository.GapEditRepository
import com.github.raininforest.data.repository.GapListRepository
import com.github.raininforest.usecases.parametercomposer.ParameterComposer
import com.github.raininforest.db.DBFactory
import com.github.raininforest.db.DBSource
import com.github.raininforest.db.DBSourceImpl
import com.github.raininforest.usecases.share.ShareService

class Dependencies(
    private val platformDependencies: PlatformDependencies
) {
    val gapListRepository: GapListRepository
        get() = GapListRepository(dbSource)

    val gapDetailsRepository: GapDetailsRepository
        get() = GapDetailsRepository(
            dbSource = dbSource,
            calculator = calculator,
            parameterComposer = parameterComposer
        )

    val gapEditRepository: GapEditRepository
        get() = GapEditRepository(dbSource)

    private val calculator: Calculator
        get() = CalculatorImpl()

    private val parameterComposer: ParameterComposer
        get() = ParameterComposer()

    private val dbSource: DBSource by lazy {
        DBSourceImpl(db)
    }

    private val db: GapCalcDatabase by lazy {
        dbFactory.createDB()
    }

    private val dbFactory: DBFactory
        get() = DBFactory(platformDependencies.sqlDriverFactory)

    val shareService: ShareService by lazy { platformDependencies.shareService }
}
