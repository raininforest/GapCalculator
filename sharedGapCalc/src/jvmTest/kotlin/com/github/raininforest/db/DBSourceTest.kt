package com.github.raininforest.db

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.github.raininforest.GapCalcDatabase
import com.github.raininforest.data.entity.GapParametersEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import java.util.Properties
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DBSourceTest {

    private val driver = JdbcSqliteDriver(
        url = JdbcSqliteDriver.IN_MEMORY,
        properties = Properties(),
        schema = GapCalcDatabase.Schema.synchronous()
    )
    private val source = DBSourceImpl(GapCalcDatabase(driver), Dispatchers.Unconfined)

    @Test
    fun insertGap_createsRowWithDefaultParameters() = runTest {
        source.insertGap(title = "jump", date = "today")

        val gap = source.getGapList().single()
        assertEquals("jump", gap.title)
        assertNotNull(source.getGapParameters(gap.id))
    }

    @Test
    fun insertGap_givesEveryGapItsOwnId() = runTest {
        repeat(3) { source.insertGap(title = "same", date = "same") }

        assertEquals(3, source.getGapList().map { it.id }.toSet().size)
    }

    @Test
    fun removeGap_removesItsParametersToo() = runTest {
        source.insertGap("a", "d")
        val id = source.getGapList().single().id

        source.removeGap(id)

        assertEquals(emptyList(), source.getGapList())
        assertNull(source.getGapParameters(id))
    }

    @Test
    fun reusedRowId_doesNotInheritParametersOfRemovedGap() = runTest {
        source.insertGap("first", "d")
        val firstId = source.getGapList().single().id
        source.updateGapParameters(firstId, GapParametersEntity("9", "9", "9", "9", "9", "9", "9"))
        source.removeGap(firstId)

        source.insertGap("second", "d")
        val second = source.getGapList().single()

        assertEquals(firstId, second.id) // sqlite hands the freed id out again
        assertNotEquals("9", source.getGapParameters(second.id)?.gap)
    }

    @Test
    fun updates_arePersistedAndOnlyTouchTheirGap() = runTest {
        source.insertGap("a", "d")
        source.insertGap("b", "d")
        val (first, second) = source.getGapList()

        source.changeGapTitle(first.id, "renamed")
        source.updateGapParameters(first.id, GapParametersEntity("7", "6", "5", "4", "3", "2", "1"))

        assertEquals("renamed", source.getGap(first.id)?.title)
        assertEquals("7", source.getGapParameters(first.id)?.gap)
        assertEquals("b", source.getGap(second.id)?.title)
        assertNotEquals("7", source.getGapParameters(second.id)?.gap)
    }
}
