package com.mystic.planetexplorer.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created: Fri 05 Dec 2025
 * Author: Muhammad Ashhal
 */

@Serializable
data class PlanetResponse(
    val results: List<PlanetDto>,
    val next: String?
)

@Serializable
data class PlanetDto(
    val name: String,
    val climate: String,
    @SerialName("orbital_period") val orbitalPeriod: String,
    val gravity: String,
    val url: String
)