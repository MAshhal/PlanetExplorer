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

    private var _binding: FragmentPlanetDetailsBinding? = null
    val binding get() = _binding!!

    private val viewModel: PlanetDetailsViewModel by viewModels()

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

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is PlanetDetailsUiState.Success -> renderState(state.planet)
                        else -> {}
                    }
                }
            }
        }
    }

    private fun renderState(planet: Planet) {
        with(binding) {
            imagePlanet.load(data = "https://picsum.photos/seed/${planet.id}/900/600")
            binding.textPlanetName.text = planet.name
            binding.textPlanetClimate.text = planet.climate
                ?.replaceFirstChar { it.uppercase() }
                ?: getString(R.string.unknown_climate)
            binding.textPlanetOrbitalPeriod.text = planet.orbitalPeriod
                ?.let { "$it days" } ?: getString(R.string.unknown)
            binding.textPlanetGravity.text =
                planet.gravity ?: getString(R.string.unknown)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}