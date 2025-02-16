package com.example.feature_api_tracks.ui.search.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.core.ui.viewmodel.SharedTrackViewModel
import com.example.core.ui.fragment.BaseTracksFragment
import com.example.feature_api_tracks.R
import com.example.feature_api_tracks.di.DaggerFeatureComponent
import com.example.feature_api_tracks.ui.search.adapter.ApiTrackAdapter
import com.example.feature_api_tracks.ui.search.viewmodel.ApiTrackViewModel
import com.example.feature_api_tracks.utils.NetworkObserver
import javax.inject.Inject

class ApiTrackFragment : BaseTracksFragment<ApiTrackViewModel, ApiTrackAdapter>(
    layoutId = com.example.core.R.layout.fragment_track_list
) {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    override lateinit var viewModel: ApiTrackViewModel
    override val adapter by lazy { ApiTrackAdapter{ openPlayer(it) } }

    private val networkObserver by lazy { NetworkObserver(requireContext()) }

    private val sharedViewModel: SharedTrackViewModel by activityViewModels()

    override val screenTitle: String
        get() = getString(R.string.search_tracks_title)

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val featureComponent = DaggerFeatureComponent.factory().create()
        featureComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(ApiTrackViewModel::class.java)

        setupObservers()
        observeNetworkChanges()
        viewModel.restoreLastTracks()
    }

    override fun onResume() {
        super.onResume()
        viewModel.restoreLastTracks()
    }

    private fun setupObservers() {
        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            adapter.updateTracks(tracks)

            if (viewModel.isSearchMode()) {
                sharedViewModel.setSearchTracks(tracks)
            } else {
                sharedViewModel.setTopTracks(tracks)
            }

            binding.errorLayout.visibility = GONE
            binding.resultRecyclerView.visibility = VISIBLE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                binding.errorLayout.visibility = VISIBLE
                binding.resultRecyclerView.visibility = GONE
                binding.errorMessageTextView.text = getString(error.messageRes)
                binding.errorImageView.setImageResource(error.imageRes)
            }
        }
    }

    private fun observeNetworkChanges() {
        networkObserver.networkAvailable.observe(viewLifecycleOwner) { isAvailable ->
            if (isAvailable) viewModel.restoreLastTracks()
        }
        networkObserver.register()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        networkObserver.unregister()
    }

    override fun onSearchQueryChanged(query: String) {
        if (query.isNotEmpty()) viewModel.searchTracks(query)
        else viewModel.loadTopTracks()
    }

    override fun onSearchCleared() {
        viewModel.loadTopTracks()
    }

    private fun openPlayer(trackId: String) {
        sharedViewModel.setCurrentTrack(trackId)
        findNavController().navigate(Uri.parse("$DEEP_LINK$trackId"))
    }

    companion object {
        private const val DEEP_LINK = "musicavito://player/"
    }
}
