package com.mystic.planetexplorer.data.api

import com.mystic.planetexplorer.data.dto.PlanetDto
import com.mystic.planetexplorer.data.dto.PlanetResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created: Fri 05 Dec 2025
 * Author: Muhammad Ashhal
 */

/**
 * Retrofit service interface for SWAPI planet endpoints.
 */
interface PlanetService {

    /**
     * Fetches paginated list of planets.
     * @param page The page number (1-indexed)
     */
    @GET("planets")
    suspend fun getPlanets(
        @Query("page") page: Int = 1
    ): PlanetResponse

    /**
     * Fetches a single planet by ID.
     * @param id The planet ID
     */
    @GET("planets/{id}")
    suspend fun getPlanet(
        @Path("id") id: Int
    ): PlanetDto

}