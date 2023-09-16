package com.github.raininforest.di

import com.github.raininforest.GapCalcDatabase
import com.github.raininforest.data.GapDetailsRepository
import com.github.raininforest.data.GapEditRepository
import com.github.raininforest.data.GapListRepository
import com.github.raininforest.db.DBSourceImpl
import com.github.raininforest.db.DBSource
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
private var db: GapCalcDatabase? = null

object Dependencies {

    val gapListRepository: GapListRepository
        get() = GapListRepository(dbSource)

    val gapDetailsRepository: GapDetailsRepository
        get() = GapDetailsRepository(dbSource)

    val gapEditRepository: GapEditRepository
        get() = GapEditRepository(dbSource)

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
