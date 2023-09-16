package com.github.raininforest.db

import com.github.raininforest.GapCalcDatabase

class DBFactory(private val driverFactory: DriverFactory) {

    fun createDB(): GapCalcDatabase {
        val driver = driverFactory.createDriver()
        return GapCalcDatabase(driver)
    }
}
