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
import kotlinx.coroutines.flow.catch
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
     * Decodes planet from navigation argument.
     * Passing entire objects through navigation avoids an extra API call.
     * JSON decode failures are caught and mapped to Error state.
     */
    val uiState = _selectedPlanet
        .filterNotNull()
        .map<String, PlanetDetailsUiState> {
            PlanetDetailsUiState.Success(decodePlanetFromString(it))
        }
        .catch { exception ->
            emit(PlanetDetailsUiState.Error(exception.message ?: "Failed to load planet details"))
        }
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