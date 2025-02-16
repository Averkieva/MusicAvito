package com.example.feature_api_tracks.ui.search.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.core.data.network.NetworkObserver
import com.example.core.ui.viewmodel.SharedTrackViewModel
import com.example.core.ui.fragment.BaseTracksFragment
import com.example.feature_api_tracks.R
import com.example.feature_api_tracks.di.DaggerFeatureComponent
import com.example.feature_api_tracks.ui.search.adapter.ApiTrackAdapter
import com.example.feature_api_tracks.ui.search.viewmodel.ApiTrackViewModel
import javax.inject.Inject

/**
 * `ApiTrackFragment` – фрагмент, который отображает список треков из API.
 * Позволяет выполнять поиск, отображать топовые треки и обрабатывать ошибки.
 * Наследуется от `BaseTracksFragment`, что позволяет переиспользовать базовую логику работы с `RecyclerView`.
 */
class ApiTrackFragment : BaseTracksFragment<ApiTrackViewModel, ApiTrackAdapter>(
    layoutId = com.example.core.R.layout.fragment_track_list
) {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    override lateinit var viewModel: ApiTrackViewModel
    override val adapter by lazy { ApiTrackAdapter { openPlayer(it) } }

    /**
     * Объект для отслеживания состояния сети.
     */
    private val networkObserver by lazy { NetworkObserver(requireContext()) }

    /**
     * `SharedViewModel`, используемая между фрагментами для управления треками.
     */
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

    /**
     * Подписка на изменения списка треков и ошибок.
     */
    private fun setupObservers() {
        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            adapter.updateTracks(tracks)

            if (viewModel.isSearchMode()) {
                sharedViewModel.setSearchTracks(tracks)
            } else {
                sharedViewModel.setTopTracks(tracks)
            }

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

    /**
     * Подписка на изменения состояния сети.
     * При восстановлении соединения загружаем треки.
     */
    private fun observeNetworkChanges() {
        networkObserver.networkAvailable.observe(viewLifecycleOwner) { isAvailable ->
            if (isAvailable) viewModel.restoreLastTracks()
        }
        networkObserver.register()
    }

    /**
     * Отписка от `networkObserver`, чтобы избежать утечек памяти.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        networkObserver.unregister()
    }

    /**
     * Выполняет поиск треков при изменении поискового запроса.
     * Если запрос пустой – загружаются топовые треки.
     */
    override fun onSearchQueryChanged(query: String) {
        if (query.isNotEmpty()) viewModel.searchTracks(query)
        else viewModel.loadTopTracks()
    }

    override fun onSearchCleared() {
        viewModel.loadTopTracks()
    }

    /**
     * Открывает плеер с выбранным треком.
     *
     * @param trackId ID выбранного трека.
     */
    private fun openPlayer(trackId: String) {
        sharedViewModel.setCurrentTrack(trackId)
        findNavController().navigate(Uri.parse("$DEEP_LINK$trackId"))
    }

    companion object {
        private const val DEEP_LINK = "musicavito://player/"
    }
}