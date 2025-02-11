package com.example.feature_api_tracks.ui.search.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feature_api_tracks.databinding.FragmentApiTracksBinding
import com.example.feature_api_tracks.domain.search.model.Track

class ApiTrackFragment : Fragment() {
    private var _binding: FragmentApiTracksBinding? = null
    private val binding get() = _binding!!
    private lateinit var trackAdapter: com.example.feature_api_tracks.ui.search.adapter.TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApiTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadMockTracks()
    }

    private fun setupRecyclerView() {
        trackAdapter =
            com.example.feature_api_tracks.ui.search.adapter.TrackAdapter(emptyList()) // Пока без данных
        binding.resultRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = trackAdapter
        }
    }

    private fun loadMockTracks() {
        val mockTracks = listOf(
            Track(1.toString(), "Song One", "Artist One", " "),
            Track(2.toString(), "Song Two", "Artist Two", " "),
            Track(3.toString(), "Song Three", "Artist Three", " ")
        )
        trackAdapter.updateTracks(mockTracks)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
