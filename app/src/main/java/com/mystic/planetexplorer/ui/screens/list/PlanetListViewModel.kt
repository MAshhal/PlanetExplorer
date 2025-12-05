package com.mystic.planetexplorer.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mystic.planetexplorer.core.model.Planet
import com.mystic.planetexplorer.core.network.Dispatcher
import com.mystic.planetexplorer.core.network.DispatcherType
import com.mystic.planetexplorer.core.network.map
import com.mystic.planetexplorer.domain.usecase.GetPlanetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
): ViewModel() {
    private val _uiState = MutableStateFlow<PlanetListUiState>(PlanetListUiState.Loading)
    val uiState: StateFlow<PlanetListUiState> get() = _uiState
        .onStart { loadPlanets(); println("onStart called") }
        .flowOn(ioDispatcher) // Inject dispatcher later to run this on IO
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = _uiState.value
        )

    private suspend fun loadPlanets() {
        val planets = planetsUseCase()
        planets.map { list -> _uiState.update { PlanetListUiState.Success(list) } }
    }

}

sealed interface PlanetListUiState {
    data object Loading: PlanetListUiState
    data class Success(val planets: List<Planet>): PlanetListUiState
    data class Error(val message: String): PlanetListUiState
}