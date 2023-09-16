package com.github.raininforest.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.github.raininforest.GapCalcDatabase

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(GapCalcDatabase.Schema, DB_FILE_NAME)
    }
}
