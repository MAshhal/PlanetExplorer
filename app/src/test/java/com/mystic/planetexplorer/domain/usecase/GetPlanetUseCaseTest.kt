package com.mystic.planetexplorer.domain.usecase

import com.mystic.planetexplorer.core.model.Planet
import com.mystic.planetexplorer.core.network.Result
import com.mystic.planetexplorer.domain.repository.PlanetRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

/**
 * Unit tests for GetPlanetUseCase
 */
class GetPlanetUseCaseTest {

    private lateinit var repository: PlanetRepository
    private lateinit var useCase: GetPlanetUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetPlanetUseCase(repository)
    }

    @Test
    fun `invoke returns Success when repository returns Success`() = runTest {
        val planetId = 1
        val planet = Planet(
            id = 1,
            name = "Tatooine",
            climate = "arid",
            orbitalPeriod = 304,
            gravity = "1 standard"
        )
        coEvery { repository.getPlanet(planetId) } returns Result.Success(planet)

        val result = useCase(planetId)

        assertTrue(result is Result.Success)
        assertEquals(planet, (result as Result.Success).data)
        coVerify(exactly = 1) { repository.getPlanet(planetId) }
    }

    @Test
    fun `invoke returns Failure when repository returns Failure`() = runTest {
        val planetId = 1
        val exception = IOException("Network error")
        coEvery { repository.getPlanet(planetId) } returns Result.Failure(exception)

        val result = useCase(planetId)

        assertTrue(result is Result.Failure)
        assertEquals(exception, (result as Result.Failure).exception)
        coVerify(exactly = 1) { repository.getPlanet(planetId) }
    }

    @Test
    fun `invoke returns Loading when repository returns Loading`() = runTest {
        val planetId = 1
        coEvery { repository.getPlanet(planetId) } returns Result.Loading

        val result = useCase(planetId)

        assertTrue(result is Result.Loading)
        coVerify(exactly = 1) { repository.getPlanet(planetId) }
    }

    @Test
    fun `invoke passes correct planet id to repository`() = runTest {
        val planetId = 42
        val planet = Planet(
            id = 42,
            name = "Test Planet",
            climate = "temperate",
            orbitalPeriod = 365,
            gravity = "1 standard"
        )
        coEvery { repository.getPlanet(planetId) } returns Result.Success(planet)

        val result = useCase(planetId)

        assertTrue(result is Result.Success)
        assertEquals(42, (result as Result.Success).data.id)
        coVerify(exactly = 1) { repository.getPlanet(42) }
    }

    @Test
    fun `invoke handles planet with null values correctly`() = runTest {
        val planetId = 10
        val planet = Planet(
            id = 10,
            name = "Kamino",
            climate = null,
            orbitalPeriod = null,
            gravity = null
        )
        coEvery { repository.getPlanet(planetId) } returns Result.Success(planet)

        val result = useCase(planetId)

        assertTrue(result is Result.Success)
        val resultPlanet = (result as Result.Success).data
        assertEquals("Kamino", resultPlanet.name)
        assertEquals(null, resultPlanet.climate)
        assertEquals(null, resultPlanet.orbitalPeriod)
        assertEquals(null, resultPlanet.gravity)
    }

    @Test
    fun `invoke can be called multiple times with different ids`() = runTest {
        val planet1 = Planet(1, "Tatooine", "arid", 304, "1 standard")
        val planet2 = Planet(2, "Alderaan", "temperate", 364, "1 standard")

        coEvery { repository.getPlanet(1) } returns Result.Success(planet1)
        coEvery { repository.getPlanet(2) } returns Result.Success(planet2)

        val result1 = useCase(1)
        val result2 = useCase(2)

        assertTrue(result1 is Result.Success)
        assertTrue(result2 is Result.Success)
        assertEquals("Tatooine", (result1 as Result.Success).data.name)
        assertEquals("Alderaan", (result2 as Result.Success).data.name)
        coVerify(exactly = 1) { repository.getPlanet(1) }
        coVerify(exactly = 1) { repository.getPlanet(2) }
    }

    @Test
    fun `invoke handles various exception types correctly`() = runTest {
        val planetId = 1
        val exception = IllegalStateException("Invalid state")
        coEvery { repository.getPlanet(planetId) } returns Result.Failure(exception)

        val result = useCase(planetId)

        assertTrue(result is Result.Failure)
        assertTrue((result as Result.Failure).exception is IllegalStateException)
    }
}
