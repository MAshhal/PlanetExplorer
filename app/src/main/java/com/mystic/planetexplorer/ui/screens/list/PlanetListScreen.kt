package com.mystic.planetexplorer.ui.screens.list

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mystic.planetexplorer.core.designsystem.components.LoadingBox
import com.mystic.planetexplorer.core.model.Planet

/**
 * Created: Fri 05 Dec 2025
 * Author: Muhammad Ashhal
 */

@Composable
fun PlanetListScreen(
    viewModel: PlanetListViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    navigateToPlanetDetails: (Planet) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        snapshotFlow { uiState }
            .collect {
                println("New UI State: $it")
            }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { _ ->
        AnimatedContent(
            targetState = uiState,
            contentKey = {
                when (it) {
                    is PlanetListUiState.Loading -> "loading"
                    is PlanetListUiState.Success -> "success"
                    is PlanetListUiState.Error -> "error"
                }
            }
        ) { currentState ->
            when (currentState) {
                is PlanetListUiState.Loading ->
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LoadingBox()
                    }

                is PlanetListUiState.Success -> {
                    PlanetListContent(
                        uiState = currentState,
                        navigateToPlanetDetails = navigateToPlanetDetails
                    )
                }

                is PlanetListUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
fun PlanetListContent(
    uiState: PlanetListUiState.Success,
    modifier: Modifier = Modifier,
    navigateToPlanetDetails: (Planet) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(uiState.planets) { planet ->
            PlanetCard(
                planet = planet,
                onPlanetSelected = { navigateToPlanetDetails(planet) }
            )
        }
    }
}

//@Composable
//fun PlanetCard(
//    planet: Planet,
//    modifier: Modifier = Modifier,
//    onPlanetSelected: () -> Unit
//) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        onClick = onPlanetSelected
//    ) {
//        ListItem(
//            modifier = modifier,
//            leadingContent = {
//                AsyncImage(
//                    modifier = Modifier.clip(RoundedCornerShape(8.dp)),
//                    model = "https://picsum.photos/seed/${planet.id}/300/200",
//                    contentDescription = "Placeholder image for planet ${planet.name}"
//                )
//            },
//            headlineContent = {
//                Text(text = planet.name)
//            },
//            supportingContent = {
//                // TODO: refactor to use string resources
//                Text(text = "Climate: ${planet.climate}")
//            }
//        )
//    }
//}