package com.github.raininforest.di

import com.github.raininforest.GapCalcDatabase
import com.github.raininforest.calculator.Calculator
import com.github.raininforest.calculator.CalculatorImpl
import com.github.raininforest.data.GapDetailsRepository
import com.github.raininforest.data.GapEditRepository
import com.github.raininforest.data.GapListRepository
import com.github.raininforest.data.ParameterComposer
import com.github.raininforest.db.DBSource
import com.github.raininforest.db.DBSourceImpl
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
private var db: GapCalcDatabase? = null

object Dependencies {

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

    /**
     * Вызывать в Application.onCreate
     */
    fun initDb(database: GapCalcDatabase) {
        db = database
    }
}
