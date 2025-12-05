package com.mystic.planetexplorer.ui.screens.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.mystic.planetexplorer.R
import com.mystic.planetexplorer.core.model.Planet
import com.mystic.planetexplorer.databinding.FragmentPlanetDetailsBinding
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

        binding.buttonRetry.setOnClickListener {
            onBackPressedCallback()
        }

        // Collect UI state only when fragment is at least STARTED (lifecycle-aware)
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is PlanetDetailsUiState.Loading -> showLoading()
                        is PlanetDetailsUiState.Success -> showSuccess(state.planet)
                        is PlanetDetailsUiState.Error -> showError(state.message)
                    }
                }
            }
        }
    }

    /**
     * Shows loading state - hides content and error, shows progress bar.
     */
    private fun showLoading() {
        with(binding) {
            progressBar.isVisible = true
            scrollView.isVisible = false
            errorLayout.isVisible = false
        }
    }

    /**
     * Shows error state - hides content and loading, shows error message.
     */
    private fun showError(message: String) {
        with(binding) {
            progressBar.isVisible = false
            scrollView.isVisible = false
            errorLayout.isVisible = true
            textErrorMessage.text = message
        }
    }

    /**
     * Shows success state - hides loading and error, renders planet data.
     */
    private fun showSuccess(planet: Planet) {
        with(binding) {
            progressBar.isVisible = false
            scrollView.isVisible = true
            errorLayout.isVisible = false
        }
        renderPlanetData(planet)
    }

    /**
     * Renders planet details to UI views.
     * Handles nullable fields by displaying fallback strings.
     */
    private fun renderPlanetData(planet: Planet) {
        with(binding) {
            imagePlanet.load(data = "https://picsum.photos/seed/${planet.id}/900/600")
            textPlanetName.text = planet.name
            textPlanetClimate.text = planet.climate
                ?.replaceFirstChar { it.uppercase() }
                ?: getString(R.string.unknown_climate)
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