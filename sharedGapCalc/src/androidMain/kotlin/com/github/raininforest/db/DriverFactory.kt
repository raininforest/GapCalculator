package com.github.raininforest.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.github.raininforest.GapCalcDatabase

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(GapCalcDatabase.Schema, context, DB_FILE_NAME)
    }
}
