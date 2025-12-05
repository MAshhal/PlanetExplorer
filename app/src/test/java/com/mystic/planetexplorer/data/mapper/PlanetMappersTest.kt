package com.mystic.planetexplorer.data.mapper

import com.mystic.planetexplorer.core.model.Planet
import com.mystic.planetexplorer.data.dto.PlanetDto
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for PlanetDto to Planet domain mapping
 */
class PlanetMappersTest {

    @Test
    fun `toDomain maps all fields correctly with valid data`() {
        val dto = PlanetDto(
            name = "Tatooine",
            climate = "arid",
            orbitalPeriod = "304",
            gravity = "1 standard",
            url = "https://swapi.dev/api/planets/1/"
        )

        val result = dto.toDomain()

        val expected = Planet(
            id = 1,
            name = "Tatooine",
            climate = "arid",
            orbitalPeriod = 304,
            gravity = "1 standard"
        )
        assertEquals(expected, result)
    }

    @Test
    fun `toDomain extracts id from url without trailing slash`() {
        val dto = PlanetDto(
            name = "Alderaan",
            climate = "temperate",
            orbitalPeriod = "364",
            gravity = "1 standard",
            url = "https://swapi.dev/api/planets/2"
        )

        val result = dto.toDomain()

        assertEquals(2, result.id)
    }

    @Test
    fun `toDomain extracts id from url with trailing slash`() {
        val dto = PlanetDto(
            name = "Yavin IV",
            climate = "temperate, tropical",
            orbitalPeriod = "4818",
            gravity = "1 standard",
            url = "https://swapi.dev/api/planets/3/"
        )

        val result = dto.toDomain()

        assertEquals(3, result.id)
    }

    @Test
    fun `toDomain preserves name exactly as provided`() {
        val dto = PlanetDto(
            name = "Hoth",
            climate = "frozen",
            orbitalPeriod = "549",
            gravity = "1.1 standard",
            url = "https://swapi.dev/api/planets/4/"
        )

        val result = dto.toDomain()

        assertEquals("Hoth", result.name)
    }

    @Test
    fun `toDomain handles climate with multiple values`() {
        val dto = PlanetDto(
            name = "Dagobah",
            climate = "murky, humid, foggy",
            orbitalPeriod = "341",
            gravity = "N/A",
            url = "https://swapi.dev/api/planets/5/"
        )

        val result = dto.toDomain()

        assertEquals("murky, humid, foggy", result.climate)
    }

    @Test
    fun `toDomain maps unknown climate to null`() {
        val dto = PlanetDto(
            name = "Bespin",
            climate = "unknown",
            orbitalPeriod = "5110",
            gravity = "1.5 (surface), 1 standard (Cloud City)",
            url = "https://swapi.dev/api/planets/6/"
        )

        val result = dto.toDomain()

        assertEquals(null, result.climate)
    }

    @Test
    fun `toDomain maps unknown gravity to null`() {
        val dto = PlanetDto(
            name = "Endor",
            climate = "temperate",
            orbitalPeriod = "402",
            gravity = "unknown",
            url = "https://swapi.dev/api/planets/7/"
        )

        val result = dto.toDomain()

        assertEquals(null, result.gravity)
    }

    @Test
    fun `toDomain maps invalid orbital period to null`() {
        val dto = PlanetDto(
            name = "Naboo",
            climate = "temperate",
            orbitalPeriod = "unknown",
            gravity = "1 standard",
            url = "https://swapi.dev/api/planets/8/"
        )

        val result = dto.toDomain()

        assertEquals(null, result.orbitalPeriod)
    }

    @Test
    fun `toDomain handles multiple unknown fields`() {
        // Given
        val dto = PlanetDto(
            name = "Kamino",
            climate = "unknown",
            orbitalPeriod = "unknown",
            gravity = "unknown",
            url = "https://swapi.dev/api/planets/10/"
        )

        // When
        val result = dto.toDomain()

        // Then
        val expected = Planet(
            id = 10,
            name = "Kamino",
            climate = null,
            orbitalPeriod = null,
            gravity = null
        )
        assertEquals(expected, result)
    }
}
