package com.mystic.planetexplorer.domain.usecase

import com.mystic.planetexplorer.core.model.Planet
import com.mystic.planetexplorer.core.network.Result
import com.mystic.planetexplorer.domain.repository.PlanetRepository
import javax.inject.Inject

/**
 * Created: Fri 05 Dec 2025
 * Author: Muhammad Ashhal
 */

class GetPlanetsUseCase @Inject constructor(
    private val repository: PlanetRepository
) {
    suspend operator fun invoke(page: Int = 1): Result<List<Planet>> = repository.getPlanets(page)
}