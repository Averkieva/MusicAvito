package com.example.feature_playback_tracks.ui.player.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.feature_playback_tracks.databinding.FragmentTrackPlayerBinding
import com.example.feature_playback_tracks.di.DaggerFeatureComponent
import com.example.feature_playback_tracks.domain.model.Track
import com.example.feature_playback_tracks.ui.player.viewmodel.PlayerTrackViewModel
import javax.inject.Inject

class PlayerTrackFragment : Fragment() {

    private var _binding: FragmentTrackPlayerBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: PlayerTrackViewModel by viewModels { viewModelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val playerFeatureComponent = DaggerFeatureComponent.factory().create()
        playerFeatureComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val trackId = arguments?.getString("trackId")
        if (trackId == null) {
            Toast.makeText(requireContext(), "Ошибка: trackId не передан", Toast.LENGTH_SHORT)
                .show()
            findNavController().popBackStack()
            return
        }

        setupObservers()
        viewModel.loadTrack(trackId)
    }

    private fun setupObservers() {
        viewModel.track.observe(viewLifecycleOwner) { track ->
            track?.let { updateUI(it) }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(track: Track) {
        binding.trackTitleTextView.text = track.title
        binding.artistNameTextView.text = track.artist.name
        binding.albumNameTextView.text = track.album.title

        Glide.with(this)
            .load(track.album.cover)
            .into(binding.albumCoverImageView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
