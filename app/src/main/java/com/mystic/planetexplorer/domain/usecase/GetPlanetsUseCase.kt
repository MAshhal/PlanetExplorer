package com.mystic.planetexplorer.domain.usecase

import com.mystic.planetexplorer.core.model.Planet
import com.mystic.planetexplorer.core.network.Dispatcher
import com.mystic.planetexplorer.core.network.DispatcherType
import com.mystic.planetexplorer.core.network.Result
import com.mystic.planetexplorer.domain.repository.PlanetRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created: Fri 05 Dec 2025
 * Author: Muhammad Ashhal
 */

/**
 * Use case for retrieving paginated (when implemented) list of planets.
 * Provides a clean separation between UI layer and data layer.
 */
class GetPlanetsUseCase @Inject constructor(
    private val repository: PlanetRepository,
    @Dispatcher(DispatcherType.IO) private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(page: Int = 1): Result<List<Planet>> = withContext(ioDispatcher) {
        repository.getPlanets(page)
    }
}