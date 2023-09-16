package com.github.raininforest.android

import android.app.Application
import com.github.raininforest.db.DBFactory
import com.github.raininforest.db.DriverFactory
import com.github.raininforest.di.Dependencies

class GapCalculatorApplication : Application() {
    private fun initDB() {
        val driverFactory = DriverFactory(context = this)
        val dbFactory = DBFactory(driverFactory = driverFactory)
        val dataBase = dbFactory.createDB()
        Dependencies.initDb(database = dataBase)
    }

    override fun onCreate() {
        super.onCreate()

        initDB()
    }
}