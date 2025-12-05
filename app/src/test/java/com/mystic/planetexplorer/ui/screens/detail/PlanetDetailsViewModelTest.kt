package com.mystic.planetexplorer.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.mystic.planetexplorer.core.model.Planet
import com.mystic.planetexplorer.ui.navigation.Screens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlanetDetailsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: PlanetDetailsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun encodePlanetToString(planet: Planet): String {
        return Json.encodeToString(planet)
    }

    @Test
    fun `uiState emits Loading initially when no planet in SavedStateHandle`() = runTest {
        val savedStateHandle = SavedStateHandle()

        viewModel = PlanetDetailsViewModel(savedStateHandle)

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is PlanetDetailsUiState.Loading)
            cancel()
        }
    }

    @Test
    fun `uiState emits Success with planet when planet is in SavedStateHandle`() = runTest {

        val planet = Planet(
            id = 1,
            name = "Tatooine",
            climate = "arid",
            orbitalPeriod = 304,
            gravity = "1 standard"
        )
        val encodedPlanet = encodePlanetToString(planet)
        val savedStateHandle = SavedStateHandle(
            mapOf(Screens.PlanetDetails.EXTRA_PLANET to encodedPlanet)
        )


        viewModel = PlanetDetailsViewModel(savedStateHandle)


        viewModel.uiState.test {
            skipItems(1) // Skip loading
            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state is PlanetDetailsUiState.Success)
            val successState = state as PlanetDetailsUiState.Success
            assertEquals(1, successState.planet.id)
            assertEquals("Tatooine", successState.planet.name)
            assertEquals("arid", successState.planet.climate)
            assertEquals(304, successState.planet.orbitalPeriod)
            assertEquals("1 standard", successState.planet.gravity)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState handles planet with null values correctly`() = runTest {

        val planet = Planet(
            id = 10,
            name = "Kamino",
            climate = null,
            orbitalPeriod = null,
            gravity = null
        )
        val encodedPlanet = encodePlanetToString(planet)
        val savedStateHandle = SavedStateHandle(
            mapOf(Screens.PlanetDetails.EXTRA_PLANET to encodedPlanet)
        )

        viewModel = PlanetDetailsViewModel(savedStateHandle)

        viewModel.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state is PlanetDetailsUiState.Success)
            val successState = state as PlanetDetailsUiState.Success
            assertEquals(10, successState.planet.id)
            assertEquals("Kamino", successState.planet.name)
            assertEquals(null, successState.planet.climate)
            assertEquals(null, successState.planet.orbitalPeriod)
            assertEquals(null, successState.planet.gravity)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState handles planet with special characters in name`() = runTest {

        val planet = Planet(
            id = 5,
            name = "Dagobah (Swamp Planet)",
            climate = "murky, humid",
            orbitalPeriod = 341,
            gravity = "N/A"
        )
        val encodedPlanet = encodePlanetToString(planet)
        val savedStateHandle = SavedStateHandle(
            mapOf(Screens.PlanetDetails.EXTRA_PLANET to encodedPlanet)
        )


        viewModel = PlanetDetailsViewModel(savedStateHandle)


        viewModel.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state is PlanetDetailsUiState.Success)
            val successState = state as PlanetDetailsUiState.Success
            assertEquals("Dagobah (Swamp Planet)", successState.planet.name)
            assertEquals("murky, humid", successState.planet.climate)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState handles planet with complex climate description`() = runTest {

        val planet = Planet(
            id = 3,
            name = "Yavin IV",
            climate = "temperate, tropical, humid, rainy",
            orbitalPeriod = 4818,
            gravity = "1 standard"
        )
        val encodedPlanet = encodePlanetToString(planet)
        val savedStateHandle = SavedStateHandle(
            mapOf(Screens.PlanetDetails.EXTRA_PLANET to encodedPlanet)
        )

        viewModel = PlanetDetailsViewModel(savedStateHandle)


        viewModel.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state is PlanetDetailsUiState.Success)
            val successState = state as PlanetDetailsUiState.Success
            assertEquals("temperate, tropical, humid, rainy", successState.planet.climate)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState handles planet with large orbital period`() = runTest {

        val planet = Planet(
            id = 6,
            name = "Bespin",
            climate = "temperate",
            orbitalPeriod = 5110,
            gravity = "1.5 (surface), 1 standard (Cloud City)"
        )
        val encodedPlanet = encodePlanetToString(planet)
        val savedStateHandle = SavedStateHandle(
            mapOf(Screens.PlanetDetails.EXTRA_PLANET to encodedPlanet)
        )

        viewModel = PlanetDetailsViewModel(savedStateHandle)

        viewModel.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state is PlanetDetailsUiState.Success)
            val successState = state as PlanetDetailsUiState.Success
            assertEquals(5110, successState.planet.orbitalPeriod)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState handles different planets correctly`() = runTest {

        val planet1 = Planet(1, "Tatooine", "arid", 304, "1 standard")
        val planet2 = Planet(2, "Alderaan", "temperate", 364, "1 standard")

        val encodedPlanet1 = encodePlanetToString(planet1)
        val encodedPlanet2 = encodePlanetToString(planet2)

        // Test with first planet
        val savedStateHandle1 = SavedStateHandle(
            mapOf(Screens.PlanetDetails.EXTRA_PLANET to encodedPlanet1)
        )
        val viewModel1 = PlanetDetailsViewModel(savedStateHandle1)

        viewModel1.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()

            val state1 = awaitItem()
            assertTrue(state1 is PlanetDetailsUiState.Success)
            assertEquals("Tatooine", (state1 as PlanetDetailsUiState.Success).planet.name)

            cancel()
        }

        // Test with second planet
        val savedStateHandle2 = SavedStateHandle(
            mapOf(Screens.PlanetDetails.EXTRA_PLANET to encodedPlanet2)
        )
        val viewModel2 = PlanetDetailsViewModel(savedStateHandle2)

        viewModel2.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()

            val state2 = awaitItem()
            assertTrue(state2 is PlanetDetailsUiState.Success)
            assertEquals("Alderaan", (state2 as PlanetDetailsUiState.Success).planet.name)

            cancel()
        }
    }

    @Test
    fun `uiState preserves all planet properties correctly`() = runTest {

        val planet = Planet(
            id = 7,
            name = "Endor",
            climate = "temperate",
            orbitalPeriod = 402,
            gravity = "0.85 standard"
        )
        val encodedPlanet = encodePlanetToString(planet)
        val savedStateHandle = SavedStateHandle(
            mapOf(Screens.PlanetDetails.EXTRA_PLANET to encodedPlanet)
        )

        viewModel = PlanetDetailsViewModel(savedStateHandle)

        viewModel.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state is PlanetDetailsUiState.Success)
            val actualPlanet = (state as PlanetDetailsUiState.Success).planet

            // Verify all properties are preserved
            assertEquals(planet.id, actualPlanet.id)
            assertEquals(planet.name, actualPlanet.name)
            assertEquals(planet.climate, actualPlanet.climate)
            assertEquals(planet.orbitalPeriod, actualPlanet.orbitalPeriod)
            assertEquals(planet.gravity, actualPlanet.gravity)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `multiple collectors receive the same planet state`() = runTest {

        val planet = Planet(1, "Tatooine", "arid", 304, "1 standard")
        val encodedPlanet = encodePlanetToString(planet)
        val savedStateHandle = SavedStateHandle(
            mapOf(Screens.PlanetDetails.EXTRA_PLANET to encodedPlanet)
        )
        viewModel = PlanetDetailsViewModel(savedStateHandle)

        viewModel.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()

            val state1 = awaitItem()
            assertTrue(state1 is PlanetDetailsUiState.Success)
            assertEquals("Tatooine", (state1 as PlanetDetailsUiState.Success).planet.name)

            cancel()
        }

        // Second collector should receive the same state
        viewModel.uiState.test {
            val state2 = awaitItem()
            assertTrue(state2 is PlanetDetailsUiState.Success)
            assertEquals("Tatooine", (state2 as PlanetDetailsUiState.Success).planet.name)

            cancel()
        }
    }

    @Test
    fun `uiState handles planet with minimum data`() = runTest {

        val planet = Planet(
            id = 999,
            name = "Unknown Planet",
            climate = null,
            orbitalPeriod = null,
            gravity = null
        )
        val encodedPlanet = encodePlanetToString(planet)
        val savedStateHandle = SavedStateHandle(
            mapOf(Screens.PlanetDetails.EXTRA_PLANET to encodedPlanet)
        )

        viewModel = PlanetDetailsViewModel(savedStateHandle)

        viewModel.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state is PlanetDetailsUiState.Success)
            val successState = state as PlanetDetailsUiState.Success

            // Only id and name should be present
            assertEquals(999, successState.planet.id)
            assertEquals("Unknown Planet", successState.planet.name)
            assertEquals(null, successState.planet.climate)
            assertEquals(null, successState.planet.orbitalPeriod)
            assertEquals(null, successState.planet.gravity)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
