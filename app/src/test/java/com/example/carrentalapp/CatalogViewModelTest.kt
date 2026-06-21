package com.example.carrentalapp

import com.example.carrentalapp.model.CarDto
import com.example.carrentalapp.statemanagement.CatalogViewModel
import com.example.carrentalapp.statemanagement.SortOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var viewModel: CatalogViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CatalogViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isCorrect() = testScope.runTest {
        val state = viewModel.uiState.value
        assert(state.cars.isEmpty())
        assert(!state.isLoading)
        assert(state.error == null)
        assert(!state.isFromCache)
        assert(state.searchQuery.isEmpty())
        assert(state.sortOrder == SortOrder.DEFAULT)
    }

    @Test
    fun setSearchQuery_updatesState() = testScope.runTest {
        viewModel.setSearchQuery("Polo")
        assert(viewModel.uiState.value.searchQuery == "Polo")
    }

    @Test
    fun setSortOrder_updatesState() = testScope.runTest {
        viewModel.setSortOrder(SortOrder.PRICE_ASC)
        assert(viewModel.uiState.value.sortOrder == SortOrder.PRICE_ASC)

        viewModel.setSortOrder(SortOrder.PRICE_DESC)
        assert(viewModel.uiState.value.sortOrder == SortOrder.PRICE_DESC)

        viewModel.setSortOrder(SortOrder.DEFAULT)
        assert(viewModel.uiState.value.sortOrder == SortOrder.DEFAULT)
    }

    @Test
    fun clearSearchQuery_worksCorrectly() = testScope.runTest {
        viewModel.setSearchQuery("Test")
        assert(viewModel.uiState.value.searchQuery == "Test")

        viewModel.setSearchQuery("")
        assert(viewModel.uiState.value.searchQuery.isEmpty())
    }
}
