package com.mystic.planetexplorer.ui.navigation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.core.os.bundleOf
import androidx.fragment.compose.AndroidFragment
import androidx.fragment.compose.rememberFragmentState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mystic.planetexplorer.core.model.Planet
import com.mystic.planetexplorer.ui.screens.detail.PlanetDetailsFragment
import com.mystic.planetexplorer.ui.screens.list.PlanetListScreen
import com.mystic.planetexplorer.ui.screens.list.PlanetListViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Created: Fri 05 Dec 2025
 * Author: Muhammad Ashhal
 */

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screens.PlanetList
    ) {
        composable<Screens.PlanetList> {
            PlanetListScreen { planet ->
                navController.navigate(Screens.PlanetDetails(encodePlanetToString(planet)))
            }
        }

        // Hosts PlanetDetailsFragment in Compose navigation (Fragment-Compose interop)
        composable<Screens.PlanetDetails> {
            val args = it.toRoute<Screens.PlanetDetails>()
            val fragmentState = rememberFragmentState()

            AndroidFragment<PlanetDetailsFragment>(
                modifier = Modifier.fillMaxSize(),
                fragmentState = fragmentState,
                arguments = bundleOf(Screens.PlanetDetails.EXTRA_PLANET to args.planet)
            ) { fragment ->
                fragment.onBackPressedCallback = { navController.navigateUp() }
            }
        }
    }
}

/**
 * Type-safe navigation screens using Kotlin serialization.
 * Planet object is serialized to JSON string for safe navigation argument passing.
 */
@Serializable
sealed interface Screens {
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