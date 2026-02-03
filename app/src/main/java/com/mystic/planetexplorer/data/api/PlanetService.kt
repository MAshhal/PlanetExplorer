package com.mystic.planetexplorer.data.api

import com.mystic.planetexplorer.data.dto.PlanetDto
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
     * Fetches list of planets.
     */
    @GET("planets")
    suspend fun getPlanets(): List<PlanetDto>

    /**
     * Fetches a single planet by ID.
     * @param id The planet ID
     */
    @GET("planets/{id}")
    suspend fun getPlanet(
        @Path("id") id: Int
    ): PlanetDto

}