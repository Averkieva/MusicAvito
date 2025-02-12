package com.example.feature_api_tracks.ui.search.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feature_api_tracks.data.api.RetrofitClient
import com.example.feature_api_tracks.data.repository.ApiTrackRepositoryImpl
import com.example.feature_api_tracks.databinding.FragmentApiTracksBinding
import com.example.feature_api_tracks.ui.search.adapter.ApiTrackAdapter
import com.example.feature_api_tracks.ui.search.viewmodel.ApiTrackViewModel
import com.example.feature_api_tracks.ui.search.viewmodel.ApiViewModelFactory

class ApiTrackFragment : Fragment() {
    private var _binding: FragmentApiTracksBinding? = null
    private val binding get() = _binding!!
    private lateinit var trackAdapter: ApiTrackAdapter
    private lateinit var viewModel: ApiTrackViewModel

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

        val repository = ApiTrackRepositoryImpl(RetrofitClient.api)
        viewModel =
            ViewModelProvider(this, ApiViewModelFactory(repository))[ApiTrackViewModel::class.java]

        setupRecyclerView()
        setupObservers()
        setupSearch()

        viewModel.loadTopTracks()
    }

    private fun setupRecyclerView() {
        trackAdapter = ApiTrackAdapter(emptyList())
        binding.resultRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = trackAdapter
        }
    }

    private fun setupObservers() {
        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            trackAdapter.updateTracks(tracks)
            binding.errorLayout.visibility = View.GONE
            binding.resultRecyclerView.visibility = View.VISIBLE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                binding.errorLayout.visibility = View.VISIBLE
                binding.resultRecyclerView.visibility = View.GONE
                binding.errorMessageTextView.text = getString(error.messageRes)
                binding.errorImageView.setImageResource(error.imageRes)
            }
        }
    }


    private fun setupSearch() {
        binding.searchInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.toString()?.let { query ->
                    if (query.isNotEmpty()) viewModel.searchTracks(query)
                    else viewModel.loadTopTracks()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
