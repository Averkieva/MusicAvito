package com.example.feature_playback_tracks.ui.player.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.feature_playback_tracks.R
import com.example.feature_playback_tracks.databinding.FragmentTrackPlayerBinding
import com.example.feature_playback_tracks.di.DaggerFeatureComponent
import com.example.feature_playback_tracks.domain.model.Track
import com.example.feature_playback_tracks.ui.player.viewmodel.PlayerTrackViewModel
import com.example.feature_playback_tracks.ui.player.viewmodel.SharedTrackViewModel
import com.example.feature_playback_tracks.utils.TimeAndDateUtils
import com.example.feature_playback_tracks.utils.TimeAndDateUtils.formatTimeFromMillis
import javax.inject.Inject

class PlayerTrackFragment : Fragment() {

    private var _binding: FragmentTrackPlayerBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: PlayerTrackViewModel by viewModels { viewModelFactory }

    private val sharedViewModel: SharedTrackViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        DaggerFeatureComponent.factory()
            .create(requireActivity())
            .inject(this)
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

        val trackId = arguments?.getString(TRACK_ID_KEY)
        if (trackId == null) {
            showToast(getString(R.string.error_no_track_id))
            findNavController().popBackStack()
            return
        }

        sharedViewModel.setCurrentTrack(trackId)
        setupObservers()
        setupListeners()
        setupSeekBar()
        viewModel.loadTrack(trackId)
    }

    private fun setupObservers() {
        viewModel.track.observe(viewLifecycleOwner) { track ->
            track?.let { updateUI(it) }
        }

        viewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            binding.playPauseImageButton.setImageResource(
                if (isPlaying) R.drawable.pause_button else R.drawable.play_button
            )
        }
        sharedViewModel.currentTrackId.observe(viewLifecycleOwner) { trackId ->
            trackId?.let { viewModel.loadTrack(it, autoPlay = true) }
        }

        viewModel.trackProgress.observe(viewLifecycleOwner) { progress ->
            binding.progressSeekBar.progress = progress / TRACK_SEEK_MULTIPLIER
            binding.trackProgressTextView.text = formatTimeFromMillis(progress)
        }

        viewModel.trackDuration.observe(viewLifecycleOwner) { duration ->
            binding.trackDurationTextView.text = formatTimeFromMillis(duration)
            binding.progressSeekBar.max = duration / TRACK_SEEK_MULTIPLIER
        }
    }

    private fun setupListeners() {
        binding.playPauseImageButton.setOnClickListener {
            viewModel.togglePlayPause()
        }

        binding.nextImageButton.setOnClickListener {
            viewModel.skipToNext()
        }

        binding.previousImageButton.setOnClickListener {
            viewModel.skipToPrevious()
        }
        binding.nextTrackImageButton.setOnClickListener {
            val nextTrack = sharedViewModel.getNextTrack()
            if (nextTrack != null) {
                sharedViewModel.setCurrentTrack(nextTrack.id)
                viewModel.loadTrack(nextTrack.id, autoPlay = true)
            } else {
                showToast(getString(R.string.error_last_track))
            }
        }

        binding.previousTrackImageButton.setOnClickListener {
            val previousTrack = sharedViewModel.getPreviousTrack()
            if (previousTrack != null) {
                sharedViewModel.setCurrentTrack(previousTrack.id)
                viewModel.loadTrack(previousTrack.id, autoPlay = true)
            } else {
                showToast(getString(R.string.error_first_track))
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(track: Track) {
        binding.trackTitleTextView.text = track.title
        binding.artistNameTextView.text = track.artist.name
        binding.albumNameTextView.text = track.album.title
        binding.trackDurationValueTextView.text =
            TimeAndDateUtils.formatDuration(track.duration)
        binding.trackPositionValueTextView.text = track.trackPosition.toString()
        binding.albumNameValueTextView.text = track.album.title
        binding.releaseYearValueTextView.text = track.album.releaseDate

        Glide.with(binding.root)
            .load(track.album.cover)
            .placeholder(R.drawable.cover_placeholder)
            .error(R.drawable.cover_placeholder)
            .transform(CenterCrop(), RoundedCorners(10))
            .into(binding.albumCoverImageView)
    }

    private fun setupSeekBar() {
        binding.progressSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val newPosition = progress * TRACK_SEEK_MULTIPLIER
                    viewModel.seekTo(newPosition)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TRACK_ID_KEY = "trackId"
        private const val TRACK_SEEK_MULTIPLIER = 300
    }
}
