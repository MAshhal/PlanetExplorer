package com.mystic.planetexplorer.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mystic.planetexplorer.core.model.Planet
import com.mystic.planetexplorer.core.network.Dispatcher
import com.mystic.planetexplorer.core.network.DispatcherType
import com.mystic.planetexplorer.core.network.Result
import com.mystic.planetexplorer.domain.usecase.GetPlanetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

/**
 * Created: Fri 05 Dec 2025
 * Author: Muhammad Ashhal
 */

@HiltViewModel
class PlanetListViewModel @Inject constructor(
    private val planetsUseCase: GetPlanetsUseCase,
    @Dispatcher(DispatcherType.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _uiState = MutableStateFlow<PlanetListUiState>(PlanetListUiState.Loading)

    // StateFlow that automatically loads planets when first subscriber collects
    // Stops collection 5 seconds after last subscriber leaves to save resources
    val uiState: StateFlow<PlanetListUiState>
        get() = _uiState
            .onStart { loadPlanets() }
            .flowOn(ioDispatcher)
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = _uiState.value
            )

    /**
     * Loads planets and handles all Result states (Success/Failure/Loading).
     * Edge case: Network failures are caught and displayed as error state.
     */
    private suspend fun loadPlanets() {
        when (val result = planetsUseCase()) {
            is Result.Success -> _uiState.update { PlanetListUiState.Success(result.data) }
            is Result.Failure -> _uiState.update {
                PlanetListUiState.Error(result.exception.message ?: "Unknown error occurred")
            }

            is Result.Loading -> { /* Already in loading state */
            }
        }
    }

    /**
     * Public function to retry loading planets after an error.
     * Sets loading state and attempts to fetch data again.
     */
    fun retry() {
        viewModelScope.launch(ioDispatcher) {
            _uiState.update { PlanetListUiState.Loading }
            loadPlanets()
        }
    }

}

sealed interface PlanetListUiState {
    data object Loading: PlanetListUiState
    data class Success(val planets: List<Planet>): PlanetListUiState
    data class Error(val message: String): PlanetListUiState
}