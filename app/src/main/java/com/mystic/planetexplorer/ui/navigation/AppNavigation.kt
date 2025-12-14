package com.mystic.planetexplorer.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.compose.AndroidFragment
import androidx.fragment.compose.rememberFragmentState
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.mystic.planetexplorer.core.model.Planet
import com.mystic.planetexplorer.ui.screens.detail.PlanetDetailsFragment
import com.mystic.planetexplorer.ui.screens.list.PlanetListScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Created: Fri 05 Dec 2025
 * Author: Muhammad Ashhal
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier
) {
    val currentWindowInfo = currentWindowAdaptiveInfo()
    val directive = remember(currentWindowInfo) {
        calculatePaneScaffoldDirective(currentWindowInfo)
            .copy(horizontalPartitionSpacerSize = 0.dp)
    }

    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(directive = directive)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        sceneStrategy = listDetailStrategy,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        transitionSpec = slideTransitionSpec(),
        popTransitionSpec = slidePopTransitionSpec(),
        entryProvider = entryProvider {
            entry<Screens.PlanetList>(
                metadata = ListDetailSceneStrategy.listPane(
                    detailPlaceholder = {
                        Surface(modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Choose a planet from the list"
                                )
                            }
                        }
                    }
                )
            ) {
                PlanetListScreen(
                    navigateToPlanetDetails = { planet ->
                        backStack.add(Screens.PlanetDetails(encodePlanetToString(planet)))
                    }
                )
            }

            entry<Screens.PlanetDetails>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) { key ->
                val fragmentState = rememberFragmentState()

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AndroidFragment<PlanetDetailsFragment>(
                        modifier = Modifier.fillMaxSize(),
                        fragmentState = fragmentState,
                        arguments = bundleOf(Screens.PlanetDetails.EXTRA_PLANET to key.planet)
                    ) { fragment ->
                        fragment.onBackPressedCallback = { backStack.removeLastOrNull() }
                    }
                }
            }
        },
        modifier = modifier,
    )

}

/**
 * Type-safe navigation screens using Kotlin serialization.
 * Planet object is serialized to JSON string for safe navigation argument passing.
 */
@Serializable
sealed interface Screens: NavKey {
    @Serializable
    data object PlanetList : Screens

    @Serializable
    data class PlanetDetails(val planet: String) : Screens {
        companion object {
            const val EXTRA_PLANET = "planet_id"
        }
    }
}

/**
 * Encodes Planet object to JSON string for navigation.
 */
private fun encodePlanetToString(planet: Planet): String {
    return Json.encodeToString(planet)
}

/**
 * Decodes JSON string back to Planet object.
 * @throws kotlinx.serialization.SerializationException if JSON is malformed
 */
fun decodePlanetFromString(value: String): Planet {
    return Json.decodeFromString(value)
}