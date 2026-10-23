package com.github.raininforest.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.github.raininforest.GapCalcDatabase
import java.io.File
import java.util.Properties

/** Stores the database in the user's home directory: `~/.gapcalc/gapCalc.db`. */
class JvmDriverFactory(
    private val directory: File = File(System.getProperty("user.home"), ".gapcalc")
) : DriverFactory {
    override suspend fun createDriver(): SqlDriver {
        directory.mkdirs()
        return JdbcSqliteDriver(
            url = "jdbc:sqlite:${File(directory, DB_FILE_NAME).absolutePath}",
            properties = Properties(),
            schema = GapCalcDatabase.Schema.synchronous()
        )
    }
}
