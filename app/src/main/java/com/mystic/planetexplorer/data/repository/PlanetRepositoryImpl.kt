package com.mystic.planetexplorer.data.repository

import com.mystic.planetexplorer.core.model.Planet
import com.mystic.planetexplorer.core.network.safeApiCall
import com.mystic.planetexplorer.data.api.PlanetService
import com.mystic.planetexplorer.data.mapper.toDomain
import com.mystic.planetexplorer.domain.repository.PlanetRepository
import com.mystic.planetexplorer.core.network.Result
import com.mystic.planetexplorer.core.network.map
import javax.inject.Inject

/**
 * Created: Fri 05 Dec 2025
 * Author: Muhammad Ashhal
 */

class PlanetRepositoryImpl @Inject constructor(
    private val service: PlanetService
) : PlanetRepository {
    override suspend fun getPlanet(id: Int): Result<Planet> {
        return safeApiCall { service.getPlanet(id).toDomain() }
    }

    override suspend fun getPlanets(page: Int): Result<List<Planet>> {
        return safeApiCall { service.getPlanets(page) }
            .map { response -> response.results.map { it.toDomain() } }
    }
}