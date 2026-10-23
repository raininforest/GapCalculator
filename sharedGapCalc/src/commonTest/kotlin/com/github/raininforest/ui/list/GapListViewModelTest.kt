package com.github.raininforest.ui.list

import com.github.raininforest.data.entity.GapListItemEntity
import com.github.raininforest.data.entity.GapParametersEntity
import com.github.raininforest.data.repository.GapListRepository
import com.github.raininforest.db.DBSource
import com.github.raininforest.ui.list.data.GapListState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class GapListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(testDispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private class FakeDbSource : DBSource {
        val gaps = mutableListOf<GapListItemEntity>()
        var gate: CompletableDeferred<Unit>? = null

        override suspend fun getGapList(): List<GapListItemEntity> {
            val snapshot = gaps.toList()
            gate?.await()
            return snapshot
        }

        override suspend fun insertGap(title: String, date: String) {
            gaps += GapListItemEntity(id = gaps.size + 1L, title = title, date = date)
        }

        override suspend fun removeGap(gapId: Long) {
            gaps.removeAll { it.id == gapId }
        }

        override suspend fun getGap(gapId: Long): GapListItemEntity? = gaps.firstOrNull { it.id == gapId }
        override suspend fun changeGapTitle(gapId: Long, title: String) = Unit
        override suspend fun getGapParameters(gapId: Long): GapParametersEntity? = null
        override suspend fun insertGapParameters(gapId: Long, parameters: GapParametersEntity) = Unit
        override suspend fun updateGapParameters(gapId: Long, parameters: GapParametersEntity) = Unit
    }

    @Test
    fun emptyStorage_showsEmptyState() = runTest(testDispatcher) {
        val viewModel = GapListViewModel(GapListRepository(FakeDbSource()))
        advanceUntilIdle()

        assertIs<GapListState.GapListEmpty>(viewModel.gapList.value)
    }

    @Test
    fun addAndRemove_updateState() = runTest(testDispatcher) {
        val viewModel = GapListViewModel(GapListRepository(FakeDbSource()))

        viewModel.addClicked()
        advanceUntilIdle()
        val afterAdd = assertIs<GapListState.GapListData>(viewModel.gapList.value)
        assertEquals(1, afterAdd.gapList.size)

        viewModel.removeGapClicked(afterAdd.gapList.single().id)
        advanceUntilIdle()
        assertIs<GapListState.GapListEmpty>(viewModel.gapList.value)
    }

    @Test
    fun newerFetch_cancelsStaleOne() = runTest(testDispatcher) {
        val source = FakeDbSource()
        source.gate = CompletableDeferred() // first (init) fetch hangs with the empty snapshot
        val viewModel = GapListViewModel(GapListRepository(source))
        advanceUntilIdle()

        source.gaps += GapListItemEntity(1, "jump", "today")
        source.gate = null
        viewModel.fetchList()
        advanceUntilIdle()

        assertEquals(1, (viewModel.gapList.value as GapListState.GapListData).gapList.size)
    }
}
