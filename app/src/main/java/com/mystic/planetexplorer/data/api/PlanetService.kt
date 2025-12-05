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

interface PlanetService {

    @GET("planets")
    suspend fun getPlanets(
        @Query("page") page: Int = 1
    ): PlanetResponse

    @GET("planets/{id}")
    suspend fun getPlanet(
        @Path("id") id: Int
    ): PlanetDto

}