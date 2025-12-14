package com.mystic.planetexplorer.ui.screens.list

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mystic.planetexplorer.R
import com.mystic.planetexplorer.core.designsystem.components.ErrorBox
import com.mystic.planetexplorer.core.designsystem.components.LoadingBox
import com.mystic.planetexplorer.core.model.Planet

/**
 * Created: Fri 05 Dec 2025
 * Author: Muhammad Ashhal
 */

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PlanetListScreen(
    modifier: Modifier = Modifier,
    viewModel: PlanetListViewModel = hiltViewModel(),
    navigateToPlanetDetails: (Planet) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumFlexibleTopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                subtitle = { Text("Explore Star Wars planets") },
                actions = {
                    IconButton(
                        onClick = viewModel::updateSort
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Rounded.Sort, null)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        // AnimatedContent provides smooth transitions between Loading/Success/Error states
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
                is PlanetListUiState.Loading -> LoadingBox(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )

                is PlanetListUiState.Success -> {
                    PlanetListContent(
                        modifier = Modifier.padding(paddingValues),
                        uiState = currentState,
                        navigateToPlanetDetails = navigateToPlanetDetails
                    )
                }

                is PlanetListUiState.Error -> {
                    ErrorBox(
                        message = stringResource(R.string.error_loading_planets),
                        onRetry = { viewModel.retry() },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
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
        items(
            items = uiState.planets,
            key = { it.name } // Didn't use key as there's a chance of duplicates
        ) { planet ->
            PlanetCard(
                planet = planet,
                onPlanetSelected = { navigateToPlanetDetails(planet) }
            )
        }
    }
}