package com.github.raininforest.db

import app.cash.sqldelight.db.SqlDriver

const val DB_FILE_NAME = "gapCalc.db"

expect class DriverFactory {
    fun createDriver(): SqlDriver
}
