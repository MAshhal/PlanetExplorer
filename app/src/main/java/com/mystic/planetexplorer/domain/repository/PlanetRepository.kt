package com.mystic.planetexplorer.domain.repository

import com.mystic.planetexplorer.core.model.Planet
import com.mystic.planetexplorer.core.network.Result

/**
 * Created: Fri 05 Dec 2025
 * Author: Muhammad Ashhal
 */

/**
 * Repository interface for planet data operations.
 *
 * This interface defines the contract for fetching planet information,
 * abstracting the data source (remote API, local cache, etc.) from the domain layer.
 */
interface PlanetRepository {
    suspend fun getPlanets(page: Int = 1): Result<List<Planet>>
    suspend fun getPlanet(id: Int): Result<Planet>
}