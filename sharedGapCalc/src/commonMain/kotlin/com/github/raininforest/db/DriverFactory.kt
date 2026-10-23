package com.github.raininforest.db

import app.cash.sqldelight.db.SqlDriver

const val DB_FILE_NAME = "gapCalc.db"

interface DriverFactory {
    /** Returns a driver whose database schema already exists; suspending because the web driver is asynchronous. */
    suspend fun createDriver(): SqlDriver
}
