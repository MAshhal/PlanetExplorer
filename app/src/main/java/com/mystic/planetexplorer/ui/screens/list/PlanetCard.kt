package com.mystic.planetexplorer.ui.screens.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.mystic.planetexplorer.core.model.Planet

/**
 * Created: Fri 05 Dec 2025
 * Author: Muhammad Ashhal
 */

/**
 * Displays a planet in a card with image overlay and gradient effect.
 * Uses deterministic image URLs based on planet ID for consistent display.
 */
@Composable
fun PlanetCard(
    planet: Planet,
    onPlanetSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Deterministic Picsum image URL - same planet ID always shows same image
    val imageUrl by remember(planet.id) {
        derivedStateOf { "https://picsum.photos/seed/${planet.id}/600/400" }
    }

    Card(
        onClick = onPlanetSelected,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Image of ${planet.name}",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient overlay improves text readability over image
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = planet.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )

                // Only show climate chip if data exists
                if (!planet.climate.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(percent = 50),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = planet.climate.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}