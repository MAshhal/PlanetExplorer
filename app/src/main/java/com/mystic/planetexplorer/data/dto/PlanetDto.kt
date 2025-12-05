package com.mystic.planetexplorer.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created: Fri 05 Dec 2025
 * Author: Muhammad Ashhal
 */

/**
 * API response wrapper for paginated planet list.
 */
@Serializable
data class PlanetResponse(
    val results: List<PlanetDto>,
    val next: String? // URL to next page, null if last page
)

/**
 * Data transfer object matching SWAPI planet endpoint structure.
 * Note: All numeric fields are returned as strings by the API (e.g., "unknown", "12").
 */
@Serializable
data class PlanetDto(
    val name: String,
    val climate: String, // Can be "unknown"
    @SerialName("orbital_period") val orbitalPeriod: String, // Can be "unknown" or numeric string
    val gravity: String, // Can be "unknown" or value like "1 standard"
    val url: String // Full API URL containing planet ID
)