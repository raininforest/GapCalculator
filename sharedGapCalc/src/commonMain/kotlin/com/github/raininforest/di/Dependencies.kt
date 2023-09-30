package com.github.raininforest.di

import com.github.raininforest.GapCalcDatabase
import com.github.raininforest.calculator.Calculator
import com.github.raininforest.calculator.CalculatorImpl
import com.github.raininforest.data.GapDetailsRepository
import com.github.raininforest.data.GapEditRepository
import com.github.raininforest.data.GapListRepository
import com.github.raininforest.data.ParameterComposer
import com.github.raininforest.db.DBFactory
import com.github.raininforest.db.DBSource
import com.github.raininforest.db.DBSourceImpl
import com.github.raininforest.share.ShareService

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
