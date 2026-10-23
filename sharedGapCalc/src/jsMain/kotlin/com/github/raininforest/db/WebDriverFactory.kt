package com.github.raininforest.db

import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import com.github.raininforest.GapCalcDatabase
import org.w3c.dom.Worker

/** sql.js running in a web worker; the database lives in memory, so it is empty after a page reload. */
class WebDriverFactory : DriverFactory {
    override suspend fun createDriver(): SqlDriver {
        val driver = WebWorkerDriver(newSqlWorker())
        GapCalcDatabase.Schema.awaitCreate(driver)
        return driver
    }

    private fun newSqlWorker(): Worker =
        js("""new Worker(new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url))""")
            .unsafeCast<Worker>()
}
