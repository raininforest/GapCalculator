package com.github.raininforest.db

import com.github.raininforest.GapCalcDatabase

class DBFactory(private val driverFactory: DriverFactory) {

    suspend fun createDB(): GapCalcDatabase = GapCalcDatabase(driverFactory.createDriver())
}
