package com.mystic.planetexplorer.core.model

/**
 * Created: Fri 05 Dec 2025
 * Author: Muhammad Ashhal
 */

/**
 * Nullable values indicate that API returned "unknown" for that value
 */
data class Planet(
    val id: Int,
    val name: String,
    val climate: String?,
    val orbitalPeriod: Int?,
    val gravity: String?,
)