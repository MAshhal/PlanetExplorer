package com.mystic.planetexplorer.ui.screens.list

import app.cash.turbine.test
import com.mystic.planetexplorer.core.model.Planet
import com.mystic.planetexplorer.core.network.Result
import com.mystic.planetexplorer.domain.usecase.GetPlanetsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlanetListViewModelTest {

    private lateinit var getPlanetsUseCase: GetPlanetsUseCase
    private lateinit var viewModel: PlanetListViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getPlanetsUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState emits Loading initially`() = runTest {
        // Given
        val planets = listOf(
            Planet(1, "Tatooine", "arid", 304, "1 standard")
        )
        coEvery { getPlanetsUseCase() } returns Result.Success(planets)

        // When
        viewModel = PlanetListViewModel(getPlanetsUseCase, testDispatcher)

        // Then
        viewModel.uiState.test {
            // Initial state should be Loading
            val initial = awaitItem()
            assertTrue(initial is PlanetListUiState.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState emits Success with planets when useCase returns Success`() = runTest {
        // Given
        val planets = listOf(
            Planet(1, "Tatooine", "arid", 304, "1 standard"),
            Planet(2, "Alderaan", "temperate", 364, "1 standard")
        )
        coEvery { getPlanetsUseCase() } returns Result.Success(planets)

        // When
        viewModel = PlanetListViewModel(getPlanetsUseCase, testDispatcher)

        // Then
        viewModel.uiState.test {
            // Skip loading state
            skipItems(1)
            advanceUntilIdle()

            val success = awaitItem()
            assertTrue(success is PlanetListUiState.Success)
            val successState = success as PlanetListUiState.Success
            assertEquals(2, successState.planets.size)
            assertEquals("Tatooine", successState.planets[0].name)
            assertEquals("Alderaan", successState.planets[1].name)

            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { getPlanetsUseCase() }
    }

    @Test
    fun `uiState emits Success with empty list when useCase returns empty list`() = runTest {
        // Given
        coEvery { getPlanetsUseCase() } returns Result.Success(emptyList())

        // When
        viewModel = PlanetListViewModel(getPlanetsUseCase, testDispatcher)

        // Then
        viewModel.uiState.test {
            skipItems(1)
            advanceUntilIdle()

            val success = awaitItem()
            assertTrue(success is PlanetListUiState.Success)
            assertTrue((success as PlanetListUiState.Success).planets.isEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadPlanets is called automatically on initialization`() = runTest {
        // Given
        val planets = listOf(Planet(1, "Tatooine", "arid", 304, "1 standard"))
        coEvery { getPlanetsUseCase() } returns Result.Success(planets)

        // When
        viewModel = PlanetListViewModel(getPlanetsUseCase, testDispatcher)

        // Then
        viewModel.uiState.test {
            skipItems(1)
            advanceUntilIdle()
            awaitItem() // Should receive Success state
            cancelAndIgnoreRemainingEvents()
        }

        // Verify loadPlanets was called automatically
        coVerify(exactly = 1) { getPlanetsUseCase() }
    }

    @Test
    fun `uiState handles planets with null values correctly`() = runTest {
        // Given
        val planets = listOf(
            Planet(1, "Planet A", "arid", 304, "1 standard"),
            Planet(2, "Planet B", null, null, null),
            Planet(3, "Planet C", "temperate", null, "1 standard")
        )
        coEvery { getPlanetsUseCase() } returns Result.Success(planets)

        // When
        viewModel = PlanetListViewModel(getPlanetsUseCase, testDispatcher)

        // Then
        viewModel.uiState.test {
            skipItems(1)
            advanceUntilIdle()

            val success = awaitItem()
            assertTrue(success is PlanetListUiState.Success)
            val planetsList = (success as PlanetListUiState.Success).planets

            assertEquals(3, planetsList.size)

            // First planet
            assertEquals("Planet A", planetsList[0].name)
            assertEquals("arid", planetsList[0].climate)

            // Second planet with nulls
            assertEquals("Planet B", planetsList[1].name)
            assertEquals(null, planetsList[1].climate)
            assertEquals(null, planetsList[1].orbitalPeriod)
            assertEquals(null, planetsList[1].gravity)

            // Third planet with some nulls
            assertEquals("Planet C", planetsList[2].name)
            assertEquals("temperate", planetsList[2].climate)
            assertEquals(null, planetsList[2].orbitalPeriod)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState handles large list of planets correctly`() = runTest {
        // Given
        val planets = List(50) { index ->
            Planet(
                id = index,
                name = "Planet $index",
                climate = "Climate $index",
                orbitalPeriod = index * 100,
                gravity = "$index standard"
            )
        }
        coEvery { getPlanetsUseCase() } returns Result.Success(planets)

        // When
        viewModel = PlanetListViewModel(getPlanetsUseCase, testDispatcher)

        // Then
        viewModel.uiState.test {
            skipItems(1)
            advanceUntilIdle()

            val success = awaitItem()
            assertTrue(success is PlanetListUiState.Success)
            val planetsList = (success as PlanetListUiState.Success).planets

            assertEquals(50, planetsList.size)
            assertEquals("Planet 0", planetsList[0].name)
            assertEquals("Planet 49", planetsList[49].name)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState preserves planet order from useCase`() = runTest {
        // Given
        val planets = listOf(
            Planet(5, "Planet 5", "climate5", 500, "1 standard"),
            Planet(2, "Planet 2", "climate2", 200, "1 standard"),
            Planet(8, "Planet 8", "climate8", 800, "1 standard"),
            Planet(1, "Planet 1", "climate1", 100, "1 standard")
        )
        coEvery { getPlanetsUseCase() } returns Result.Success(planets)

        // When
        viewModel = PlanetListViewModel(getPlanetsUseCase, testDispatcher)

        // Then
        viewModel.uiState.test {
            skipItems(1)
            advanceUntilIdle()

            val success = awaitItem()
            assertTrue(success is PlanetListUiState.Success)
            val planetsList = (success as PlanetListUiState.Success).planets

            // Order should be preserved
            assertEquals(5, planetsList[0].id)
            assertEquals(2, planetsList[1].id)
            assertEquals(8, planetsList[2].id)
            assertEquals(1, planetsList[3].id)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState does not emit duplicate consecutive states`() = runTest {
        // Given
        val planets = listOf(Planet(1, "Tatooine", "arid", 304, "1 standard"))
        coEvery { getPlanetsUseCase() } returns Result.Success(planets)

        // When
        viewModel = PlanetListViewModel(getPlanetsUseCase, testDispatcher)

        // Then
        viewModel.uiState.test {
            val loading = awaitItem()
            assertTrue(loading is PlanetListUiState.Loading)

            advanceUntilIdle()

            val success = awaitItem()
            assertTrue(success is PlanetListUiState.Success)

            // Should not emit any more items since distinctUntilChanged is used
            expectNoEvents()

            cancel()
        }
    }

    @Test
    fun `multiple collectors receive the same state`() = runTest {
        // Given
        val planets = listOf(Planet(1, "Tatooine", "arid", 304, "1 standard"))
        coEvery { getPlanetsUseCase() } returns Result.Success(planets)
        viewModel = PlanetListViewModel(getPlanetsUseCase, testDispatcher)

        // When/Then - First collector
        viewModel.uiState.test {
            skipItems(1)
            advanceUntilIdle()

            val state1 = awaitItem()
            assertTrue(state1 is PlanetListUiState.Success)

            cancel()
        }

        // Second collector should receive the same state
        viewModel.uiState.test {
            val state2 = awaitItem()
            assertTrue(state2 is PlanetListUiState.Success)
            assertEquals("Tatooine", (state2 as PlanetListUiState.Success).planets[0].name)

            cancel()
        }
    }

    @Test
    fun `viewModel uses IO dispatcher for loading planets`() = runTest {
        // Given
        val planets = listOf(Planet(1, "Tatooine", "arid", 304, "1 standard"))
        coEvery { getPlanetsUseCase() } returns Result.Success(planets)
        val customDispatcher = StandardTestDispatcher()

        // When
        viewModel = PlanetListViewModel(getPlanetsUseCase, customDispatcher)

        // Then
        viewModel.uiState.test {
            skipItems(1)
            customDispatcher.scheduler.advanceUntilIdle()

            val success = awaitItem()
            assertTrue(success is PlanetListUiState.Success)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
