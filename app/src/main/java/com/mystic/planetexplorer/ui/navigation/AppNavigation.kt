package com.mystic.planetexplorer.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.os.bundleOf
import androidx.fragment.compose.AndroidFragment
import androidx.fragment.compose.rememberFragmentState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mystic.planetexplorer.ui.screens.detail.PlanetDetailsFragment
import com.mystic.planetexplorer.ui.screens.list.PlanetListScreen
import kotlinx.serialization.Serializable

/**
 * Created: Fri 05 Dec 2025
 * Author: Muhammad Ashhal
 */

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier
) {
    val state = rememberNavController()

    NavHost(
        modifier = modifier,
        navController = state,
        startDestination = Screens.PlanetList
    ) {
        composable<Screens.PlanetList> {
            PlanetListScreen()
        }

        composable<Screens.PlanetDetails> {
            val args = it.toRoute<Screens.PlanetDetails>()

            val fragmentState = rememberFragmentState()

            AndroidFragment<PlanetDetailsFragment>(
                modifier = Modifier.fillMaxSize(),
                fragmentState = fragmentState,
                arguments = bundleOf(Screens.PlanetDetails.EXTRA_PLANET_ID to args.id)
            )
        }
    }
}

@Serializable
sealed interface Screens {
    @Serializable
    data object PlanetList: Screens

    @Serializable
    data class PlanetDetails(val id: Int): Screens {
        companion object {
            const val EXTRA_PLANET_ID = "planet_id"
        }
    }
}