package com.mystic.planetexplorer.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mystic.planetexplorer.core.model.Planet
import com.mystic.planetexplorer.ui.navigation.Screens
import com.mystic.planetexplorer.ui.navigation.decodePlanetFromString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

/**
 * Created: Fri 05 Dec 2025
 * Author: Muhammad Ashhal
 */

@HiltViewModel
class PlanetDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _selectedPlanet =
        savedStateHandle.getStateFlow<String?>(Screens.PlanetDetails.EXTRA_PLANET, null)

    /**
     * Not really a good practice to pass entire objects
     * However, I'm doing it to save an API call
     */
    val uiState = _selectedPlanet
        .filterNotNull()
        .map { PlanetDetailsUiState.Success(decodePlanetFromString(it)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = PlanetDetailsUiState.Loading
        )
}

sealed interface PlanetDetailsUiState {
    data object Loading : PlanetDetailsUiState
    data class Success(val planet: Planet) : PlanetDetailsUiState
    data class Error(val message: String) : PlanetDetailsUiState
}