package com.mystic.planetexplorer.domain.usecase

import com.mystic.planetexplorer.core.model.Planet
import com.mystic.planetexplorer.core.network.Result
import com.mystic.planetexplorer.domain.repository.PlanetRepository
import javax.inject.Inject

/**
 * Created: Fri 05 Dec 2025
 * Author: Muhammad Ashhal
 */

/**
 * Use case for retrieving a single planet by ID.
 * Note: Currently unused as planet details are passed via navigation to avoid extra API call.
 */
class GetPlanetUseCase @Inject constructor(
    private val repository: PlanetRepository
) {
    suspend operator fun invoke(id: Int): Result<Planet> = repository.getPlanet(id)
}