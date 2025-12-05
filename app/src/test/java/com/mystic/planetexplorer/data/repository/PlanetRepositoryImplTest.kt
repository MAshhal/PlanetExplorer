package com.mystic.planetexplorer.data.repository

import com.mystic.planetexplorer.core.network.Result
import com.mystic.planetexplorer.data.api.PlanetService
import com.mystic.planetexplorer.data.dto.PlanetDto
import com.mystic.planetexplorer.data.dto.PlanetResponse
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
 * Unit tests for PlanetRepositoryImpl
 */
class PlanetRepositoryImplTest {

    private lateinit var service: PlanetService
    private lateinit var repository: PlanetRepositoryImpl

    @Before
    fun setup() {
        service = mockk()
        repository = PlanetRepositoryImpl(service)
    }

    // below are GetPlanet test cases

    @Test
    fun `getPlanet returns Success when service call succeeds`() = runTest {
        val planetId = 1
        val dto = PlanetDto(
            name = "Tatooine",
            climate = "arid",
            orbitalPeriod = "304",
            gravity = "1 standard",
            url = "https://swapi.dev/api/planets/1/"
        )
        coEvery { service.getPlanet(planetId) } returns dto

        val result = repository.getPlanet(planetId)

        assertTrue(result is Result.Success)
        val planet = (result as Result.Success).data
        assertEquals(1, planet.id)
        assertEquals("Tatooine", planet.name)
        assertEquals("arid", planet.climate)
        assertEquals(304, planet.orbitalPeriod)
        assertEquals("1 standard", planet.gravity)
        coVerify(exactly = 1) { service.getPlanet(planetId) }
    }

    @Test
    fun `getPlanet returns Failure when service throws IOException`() = runTest {
        val planetId = 1
        val exception = IOException("Network error")
        coEvery { service.getPlanet(planetId) } throws exception

        val result = repository.getPlanet(planetId)

        assertTrue(result is Result.Failure)
        assertEquals(exception, (result as Result.Failure).exception)
        coVerify(exactly = 1) { service.getPlanet(planetId) }
    }

    @Test
    fun `getPlanet returns Failure when service throws RuntimeException`() = runTest {
        val planetId = 1
        val exception = RuntimeException("Unexpected error")
        coEvery { service.getPlanet(planetId) } throws exception

        val result = repository.getPlanet(planetId)

        assertTrue(result is Result.Failure)
        assertEquals(exception, (result as Result.Failure).exception)
    }

    @Test
    fun `getPlanet maps unknown values to null correctly`() = runTest {
        val planetId = 10
        val dto = PlanetDto(
            name = "Kamino",
            climate = "unknown",
            orbitalPeriod = "unknown",
            gravity = "unknown",
            url = "https://swapi.dev/api/planets/10/"
        )
        coEvery { service.getPlanet(planetId) } returns dto

        val result = repository.getPlanet(planetId)

        assertTrue(result is Result.Success)
        val planet = (result as Result.Success).data
        assertEquals(null, planet.climate)
        assertEquals(null, planet.orbitalPeriod)
        assertEquals(null, planet.gravity)
    }

    // --- GetPlanets test cases ---

    @Test
    fun `getPlanets returns Success with list when service call succeeds`() = runTest {
        val page = 1
        val dtoList = listOf(
            PlanetDto(
                name = "Tatooine",
                climate = "arid",
                orbitalPeriod = "304",
                gravity = "1 standard",
                url = "https://swapi.dev/api/planets/1/"
            ),
            PlanetDto(
                name = "Alderaan",
                climate = "temperate",
                orbitalPeriod = "364",
                gravity = "1 standard",
                url = "https://swapi.dev/api/planets/2/"
            )
        )
        val response = PlanetResponse(
            results = dtoList,
            next = "https://swapi.dev/api/planets/?page=2"
        )
        coEvery { service.getPlanets(page) } returns response

        val result = repository.getPlanets(page)

        assertTrue(result is Result.Success)
        val planets = (result as Result.Success).data
        assertEquals(2, planets.size)

        val firstPlanet = planets[0]
        assertEquals(1, firstPlanet.id)
        assertEquals("Tatooine", firstPlanet.name)
        assertEquals("arid", firstPlanet.climate)

        val secondPlanet = planets[1]
        assertEquals(2, secondPlanet.id)
        assertEquals("Alderaan", secondPlanet.name)
        assertEquals("temperate", secondPlanet.climate)

        coVerify(exactly = 1) { service.getPlanets(page) }
    }

    @Test
    fun `getPlanets returns Success with empty list when no results`() = runTest {
        val page = 100
        val response = PlanetResponse(
            results = emptyList(),
            next = null
        )
        coEvery { service.getPlanets(page) } returns response

        val result = repository.getPlanets(page)

        assertTrue(result is Result.Success)
        val planets = (result as Result.Success).data
        assertTrue(planets.isEmpty())
    }

    @Test
    fun `getPlanets returns Failure when service throws exception`() = runTest {
        val page = 1
        val exception = IOException("Network timeout")
        coEvery { service.getPlanets(page) } throws exception

        val result = repository.getPlanets(page)

        assertTrue(result is Result.Failure)
        assertEquals(exception, (result as Result.Failure).exception)
        coVerify(exactly = 1) { service.getPlanets(page) }
    }

    @Test
    fun `getPlanets uses default page parameter`() = runTest {

        val response = PlanetResponse(
            results = listOf(
                PlanetDto(
                    name = "Tatooine",
                    climate = "arid",
                    orbitalPeriod = "304",
                    gravity = "1 standard",
                    url = "https://swapi.dev/api/planets/1/"
                )
            ),
            next = null
        )
        coEvery { service.getPlanets(1) } returns response

        val result = repository.getPlanets(1)

        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { service.getPlanets(1) }
    }

    @Test
    fun `getPlanets correctly maps multiple planets with mixed known and unknown values`() =
        runTest {
            val dtoList = listOf(
                PlanetDto(
                    name = "Hoth",
                    climate = "frozen",
                    orbitalPeriod = "549",
                    gravity = "1.1 standard",
                    url = "https://swapi.dev/api/planets/4/"
                ),
                PlanetDto(
                    name = "Dagobah",
                    climate = "unknown", // It's not actually unknown, just for test :)
                    orbitalPeriod = "341",
                    gravity = "unknown",
                    url = "https://swapi.dev/api/planets/5/"
                ),
                PlanetDto(
                    name = "Bespin",
                    climate = "temperate",
                    orbitalPeriod = "5110",
                    gravity = "1.5 (surface), 1 standard (Cloud City)",
                    url = "https://swapi.dev/api/planets/6/"
                )
            )
            val response = PlanetResponse(results = dtoList, next = null)
            coEvery { service.getPlanets(1) } returns response

            val result = repository.getPlanets(1)

            assertTrue(result is Result.Success)
            val planets = (result as Result.Success).data
            assertEquals(3, planets.size)

            val (firstPlanet, secondPlanet, thirdPlanet) = planets.slice(0..2)

            // First planet - all values known
            assertEquals("Hoth", firstPlanet.name)
            assertEquals("frozen", firstPlanet.climate)
            assertEquals(549, firstPlanet.orbitalPeriod)
            assertEquals("1.1 standard", firstPlanet.gravity)

            // Second planet - some unknown values
            assertEquals("Dagobah", secondPlanet.name)
            assertEquals(null, secondPlanet.climate)
            assertEquals(341, secondPlanet.orbitalPeriod)
            assertEquals(null, secondPlanet.gravity)

            // Third planet - all values known
            assertEquals("Bespin", thirdPlanet.name)
            assertEquals("temperate", thirdPlanet.climate)
            assertEquals(5110, thirdPlanet.orbitalPeriod)
        }

    @Test
    fun `getPlanets handles different page numbers correctly`() = runTest {
        val page2Response = PlanetResponse(
            results = listOf(
                PlanetDto(
                    name = "Yavin IV",
                    climate = "temperate, tropical",
                    orbitalPeriod = "4818",
                    gravity = "1 standard",
                    url = "https://swapi.dev/api/planets/3/"
                )
            ),
            next = "https://swapi.dev/api/planets/?page=3"
        )
        coEvery { service.getPlanets(2) } returns page2Response

        val result = repository.getPlanets(2)

        assertTrue(result is Result.Success)
        val planets = (result as Result.Success).data
        assertEquals(1, planets.size)
        assertEquals("Yavin IV", planets[0].name)
        coVerify(exactly = 1) { service.getPlanets(2) }
    }
}
