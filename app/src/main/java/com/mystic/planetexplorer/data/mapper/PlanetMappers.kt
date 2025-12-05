package com.mystic.planetexplorer.data.mapper

import com.mystic.planetexplorer.core.model.Planet
import com.mystic.planetexplorer.data.dto.PlanetDto

/**
 * Created: Fri 05 Dec 2025
 * Author: Muhammad Ashhal
 */

/**
 * Converts a PlanetDto to a Planet domain model.
 *
 * The function extracts the planet ID from the API URL and handles "unknown" values
 * by converting them to null for optional fields.
 *
 */
fun PlanetDto.toDomain(): Planet {
    val idFromUrl = url
        .trimEnd('/') // URL might have trailing slash at the end
        .substringAfterLast("/") // take string after last /
        .toInt()

    return Planet(
        id = idFromUrl,
        name = name,
        climate = climate.takeUnless { it.equals("unknown", ignoreCase = true) },
        orbitalPeriod = orbitalPeriod.toIntOrNull(),
        gravity = gravity.takeUnless { it.equals("unknown", ignoreCase = true) }
    )
}