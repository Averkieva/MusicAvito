package com.example.feature_download_tracks.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.core.ui.viewmodel.SharedTrackViewModel
import com.example.core.ui.fragment.BaseTracksFragment
import com.example.feature_download_tracks.R
import com.example.feature_download_tracks.ui.viewmodel.DownloadedTracksViewModel
import com.example.feature_download_tracks.di.DaggerDownloadedTracksComponent
import com.example.feature_download_tracks.di.DownloadedTracksViewModelFactory
import com.example.feature_download_tracks.di.ViewModelModule
import com.example.feature_download_tracks.domain.repository.DownloadedTracksRepository
import com.example.feature_download_tracks.ui.adapter.DownloadedTracksAdapter
import javax.inject.Inject

/**
 * `DownloadedTracksFragment` – фрагмент для отображения загруженных треков.
 * Позволяет искать загруженные треки и открывать их в плеере.
 * Наследуется от `BaseTracksFragment`, который управляет `RecyclerView` и поиском.
 */
class DownloadedTracksFragment :
    BaseTracksFragment<DownloadedTracksViewModel, DownloadedTracksAdapter>(
        layoutId = com.example.core.R.layout.fragment_track_list
    ) {

    @Inject
    lateinit var repository: DownloadedTracksRepository

    override lateinit var viewModel: DownloadedTracksViewModel

    override lateinit var adapter: DownloadedTracksAdapter

    override val screenTitle: String
        get() = getString(R.string.saved_tracks_title)

    /**
     * `SharedViewModel`, используемая между фрагментами для управления состоянием списка треков.
     */
    private val sharedViewModel: SharedTrackViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val component = DaggerDownloadedTracksComponent.builder()
            .viewModelModule(ViewModelModule(requireContext()))
            .build()
        component.inject(this)

        viewModel = ViewModelProvider(
            this,
            DownloadedTracksViewModelFactory(repository)
        )[DownloadedTracksViewModel::class.java]
        adapter = DownloadedTracksAdapter { openPlayer(it) }
    }

    /**
     * Подписка на `LiveData` ViewModel для обновления UI при изменении списка треков.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.searchResults.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isNullOrEmpty()) {
                binding.errorLayout.visibility = View.VISIBLE
                binding.resultRecyclerView.visibility = View.GONE
            } else {
                binding.errorLayout.visibility = View.GONE
                binding.resultRecyclerView.visibility = View.VISIBLE
                adapter.updateTracks(tracks)

                sharedViewModel.setFilteredDownloadedTracks(tracks)
            }
        }
    }

    /**
     * Выполняет поиск загруженных треков при изменении поискового запроса.
     */
    override fun onSearchQueryChanged(query: String) {
        viewModel.searchTracks(query)
    }

    /**
     * Загружает все загруженные треки при очистке поиска.
     */
    override fun onSearchCleared() {
        viewModel.loadDownloadedTracks()
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