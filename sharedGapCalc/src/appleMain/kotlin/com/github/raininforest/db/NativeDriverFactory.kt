package com.github.raininforest.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.github.raininforest.GapCalcDatabase

class NativeDriverFactory : DriverFactory {
    override suspend fun createDriver(): SqlDriver =
        NativeSqliteDriver(GapCalcDatabase.Schema.synchronous(), DB_FILE_NAME)
}
