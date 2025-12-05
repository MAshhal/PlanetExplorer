package com.mystic.planetexplorer.data.mapper

import com.mystic.planetexplorer.core.model.Planet
import com.mystic.planetexplorer.data.dto.PlanetDto

/**
 * Created: Fri 05 Dec 2025
 * Author: Muhammad Ashhal
 */

fun PlanetDto.toDomain(): Planet {
    val idFromUrl = url
        .trimEnd('/')
        .substringAfterLast("/")
        .toInt()

    return Planet(
        id = idFromUrl,
        name = name,
        climate = climate.takeUnless { it == "unknown" },
        orbitalPeriod = orbitalPeriod.toIntOrNull(),
        gravity = gravity.takeUnless { it == "unknown" }
    )
}