package com.mystic.planetexplorer.core.model

import kotlinx.serialization.Serializable

/**
 * Created: Fri 05 Dec 2025
 * Author: Muhammad Ashhal
 */

/**
 * Domain model for a planet with properly typed fields.
 * Nullable values indicate that API returned "unknown" for that field.
 */
@Serializable
data class Planet(
    val id: Int,
    val name: String,
    val climate: String?,
    val orbitalPeriod: Int?, // null if unknown or non-numeric
    val gravity: String?,
)