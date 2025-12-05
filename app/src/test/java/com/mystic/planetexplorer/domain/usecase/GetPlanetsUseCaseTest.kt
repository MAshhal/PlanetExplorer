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

class GetPlanetsUseCaseTest {

    private lateinit var repository: PlanetRepository
    private lateinit var useCase: GetPlanetsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetPlanetsUseCase(repository)
    }

    @Test
    fun `invoke returns Success with planets when repository returns Success`() = runTest {
        val page = 1
        val planets = listOf(
            Planet(1, "Tatooine", "arid", 304, "1 standard"),
            Planet(2, "Alderaan", "temperate", 364, "1 standard")
        )
        coEvery { repository.getPlanets(page) } returns Result.Success(planets)

        val result = useCase(page)

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(2, data.size)
        assertEquals("Tatooine", data[0].name)
        assertEquals("Alderaan", data[1].name)
        coVerify(exactly = 1) { repository.getPlanets(page) }
    }

    @Test
    fun `invoke returns Success with empty list when repository returns empty list`() = runTest {
        val page = 100
        coEvery { repository.getPlanets(page) } returns Result.Success(emptyList())

        val result = useCase(page)

        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.isEmpty())
        coVerify(exactly = 1) { repository.getPlanets(page) }
    }

    @Test
    fun `invoke returns Failure when repository returns Failure`() = runTest {
        val page = 1
        val exception = IOException("Network error")
        coEvery { repository.getPlanets(page) } returns Result.Failure(exception)

        val result = useCase(page)

        assertTrue(result is Result.Failure)
        assertEquals(exception, (result as Result.Failure).exception)
        coVerify(exactly = 1) { repository.getPlanets(page) }
    }

    @Test
    fun `invoke returns Loading when repository returns Loading`() = runTest {
        val page = 1
        coEvery { repository.getPlanets(page) } returns Result.Loading

        val result = useCase(page)

        assertTrue(result is Result.Loading)
        coVerify(exactly = 1) { repository.getPlanets(page) }
    }

    @Test
    fun `invoke uses default page parameter of 1`() = runTest {
        val planets = listOf(
            Planet(1, "Tatooine", "arid", 304, "1 standard")
        )
        coEvery { repository.getPlanets(1) } returns Result.Success(planets)

        val result = useCase()

        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { repository.getPlanets(1) }
    }

    @Test
    fun `invoke passes correct page number to repository`() = runTest {
        val page = 3
        val planets = listOf(
            Planet(21, "Planet 21", "temperate", 400, "1 standard"),
            Planet(22, "Planet 22", "frozen", 500, "1.2 standard")
        )
        coEvery { repository.getPlanets(page) } returns Result.Success(planets)

        val result = useCase(page)

        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { repository.getPlanets(3) }
    }

    @Test
    fun `invoke handles planets with null values correctly`() = runTest {
        val planets = listOf(
            Planet(1, "Planet A", "arid", 304, "1 standard"),
            Planet(2, "Planet B", null, null, null),
            Planet(3, "Planet C", "temperate", 365, null)
        )
        coEvery { repository.getPlanets(1) } returns Result.Success(planets)

        val result = useCase(1)

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(3, data.size)

        val (firstPlanet, secondPlanet, thirdPlanet) = planets.slice(0..2)
        // First planet - all values present
        assertEquals("Planet A", firstPlanet.name)
        assertEquals("arid", firstPlanet.climate)
        assertEquals(304, firstPlanet.orbitalPeriod)
        assertEquals("1 standard", firstPlanet.gravity)

        // Second planet - all nullable values are null
        assertEquals("Planet B", secondPlanet.name)
        assertEquals(null, secondPlanet.climate)
        assertEquals(null, secondPlanet.orbitalPeriod)
        assertEquals(null, secondPlanet.gravity)

        // Third planet - some nullable values
        assertEquals("Planet C", thirdPlanet.name)
        assertEquals("temperate", thirdPlanet.climate)
        assertEquals(365, thirdPlanet.orbitalPeriod)
        assertEquals(null, thirdPlanet.gravity)
    }

    @Test
    fun `invoke can be called multiple times with different pages`() = runTest {
        val planetsPage1 = listOf(Planet(1, "Tatooine", "arid", 304, "1 standard"))
        val planetsPage2 = listOf(Planet(11, "Kamino", "temperate", 463, "1 standard"))

        coEvery { repository.getPlanets(1) } returns Result.Success(planetsPage1)
        coEvery { repository.getPlanets(2) } returns Result.Success(planetsPage2)

        val result1 = useCase(1)
        val result2 = useCase(2)

        assertTrue(result1 is Result.Success)
        assertTrue(result2 is Result.Success)
        assertEquals("Tatooine", (result1 as Result.Success).data.first().name)
        assertEquals("Kamino", (result2 as Result.Success).data.first().name)
        coVerify(exactly = 1) { repository.getPlanets(1) }
        coVerify(exactly = 1) { repository.getPlanets(2) }
    }

    @Test
    fun `invoke handles large lists correctly`() = runTest {
        val planets = List(100) { index ->
            Planet(
                id = index,
                name = "Planet $index",
                climate = if (index % 2 == 0) "temperate" else null,
                orbitalPeriod = if (index % 3 == 0) index * 100 else null,
                gravity = if (index % 5 == 0) "1 standard" else null
            )
        }
        coEvery { repository.getPlanets(1) } returns Result.Success(planets)

        val result = useCase(1)

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(100, data.size)
        assertEquals("Planet 0", data[0].name)
        assertEquals("Planet 99", data[99].name)
    }

    @Test
    fun `invoke handles various exception types correctly`() = runTest {
        val page = 1
        val exception = IllegalArgumentException("Invalid page number")
        coEvery { repository.getPlanets(page) } returns Result.Failure(exception)

        val result = useCase(page)

        assertTrue(result is Result.Failure)
        assertTrue((result as Result.Failure).exception is IllegalArgumentException)
        assertEquals("Invalid page number", exception.message)
    }

    @Test
    fun `invoke preserves planet order from repository`() = runTest {
        val planets = listOf(
            Planet(3, "Yavin IV", "temperate", 4818, "1 standard"),
            Planet(1, "Tatooine", "arid", 304, "1 standard"),
            Planet(2, "Alderaan", "temperate", 364, "1 standard")
        )
        coEvery { repository.getPlanets(1) } returns Result.Success(planets)

        val result = useCase(1)

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(3, data[0].id)
        assertEquals(1, data[1].id)
        assertEquals(2, data[2].id)
    }
}
