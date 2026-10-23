package com.github.raininforest.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.github.raininforest.GapCalcDatabase

class AndroidDriverFactory(private val context: Context) : DriverFactory {
    override suspend fun createDriver(): SqlDriver =
        AndroidSqliteDriver(GapCalcDatabase.Schema.synchronous(), context, DB_FILE_NAME)
}
