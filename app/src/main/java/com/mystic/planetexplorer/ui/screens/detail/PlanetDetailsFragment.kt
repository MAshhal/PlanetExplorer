package com.mystic.planetexplorer.ui.screens.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.mystic.planetexplorer.R
import com.mystic.planetexplorer.core.model.Planet
import com.mystic.planetexplorer.databinding.FragmentPlanetDetailsBinding
import com.mystic.planetexplorer.ui.navigation.Screens
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Created: Fri 05 Dec 2025
 * Author: Muhammad Ashhal
 */

@AndroidEntryPoint
class PlanetDetailsFragment : Fragment() {

    // Nullable binding pattern to prevent memory leaks
    private var _binding: FragmentPlanetDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlanetDetailsViewModel by viewModels()

    // Callback set by navigation host to handle back navigation
    lateinit var onBackPressedCallback: () -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanetDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.setNavigationOnClickListener { onBackPressedCallback() }

        // Collect UI state only when fragment is at least STARTED (lifecycle-aware)
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is PlanetDetailsUiState.Success -> renderState(state.planet)
                        is PlanetDetailsUiState.Error -> {
                            // TODO: Show error state in UI
                        }

                        is PlanetDetailsUiState.Loading -> {
                            // TODO: Show loading state in UI
                        }
                    }
                }
            }
        }
    }

    /**
     * Renders planet details to UI views.
     * Handles nullable fields by displaying fallback strings.
     */
    private fun renderState(planet: Planet) {
        with(binding) {
            imagePlanet.load(data = "https://picsum.photos/seed/${planet.id}/900/600")
            textPlanetName.text = planet.name
            // Capitalize first letter of climate, or show "Unknown" if null
            textPlanetClimate.text = planet.climate
                ?.replaceFirstChar { it.uppercase() }
                ?: getString(R.string.unknown_climate)
            // Format orbital period with "days" suffix, or show "Unknown" if null
            textPlanetOrbitalPeriod.text = planet.orbitalPeriod
                ?.let { "$it days" }
                ?: getString(R.string.unknown)
            textPlanetGravity.text = planet.gravity ?: getString(R.string.unknown)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Prevent memory leaks by nulling binding reference
        _binding = null
    }
}