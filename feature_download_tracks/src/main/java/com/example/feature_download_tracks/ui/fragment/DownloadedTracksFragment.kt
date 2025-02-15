package com.example.feature_download_tracks.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.feature_download_tracks.ui.viewmodel.DownloadedTracksViewModel
import com.example.feature_download_tracks.databinding.FragmentDownloadedTracksBinding
import com.example.feature_download_tracks.di.DaggerDownloadedTracksComponent
import com.example.feature_download_tracks.di.DownloadedTracksViewModelFactory
import com.example.feature_download_tracks.di.ViewModelModule
import com.example.feature_download_tracks.domain.repository.DownloadedTracksRepository
import com.example.feature_download_tracks.ui.adapter.DownloadedTracksAdapter
import com.example.feature_playback_tracks.ui.player.viewmodel.SharedTrackViewModel
import javax.inject.Inject

class DownloadedTracksFragment : Fragment() {

    private var _binding: FragmentDownloadedTracksBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var repository: DownloadedTracksRepository

    private lateinit var viewModel: DownloadedTracksViewModel
    private lateinit var adapter: DownloadedTracksAdapter

    private val sharedViewModel: SharedTrackViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val component = DaggerDownloadedTracksComponent.builder()
            .viewModelModule(ViewModelModule(requireContext()))
            .build()
        component.inject(this)

        viewModel = ViewModelProvider(this, DownloadedTracksViewModelFactory(repository))[DownloadedTracksViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDownloadedTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.resultRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = DownloadedTracksAdapter { trackId ->
            openPlayer(trackId)
        }

        binding.resultRecyclerView.adapter = adapter

        setupSearch()

        viewModel.searchResults.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isNullOrEmpty()) {
                binding.errorLayout.visibility = View.VISIBLE
                binding.resultRecyclerView.visibility = View.GONE
            } else {
                binding.errorLayout.visibility = View.GONE
                binding.resultRecyclerView.visibility = View.VISIBLE
                adapter.submitList(tracks)
                sharedViewModel.setFilteredDownloadedTracks(tracks)
            }
        }

        binding.resultRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) {
                    hideKeyboard()
                }
            }
        })

        binding.root.setOnTouchListener { _, _ ->
            hideKeyboard()
            false
        }
    }

    private fun setupSearch() {
        binding.searchInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                binding.cancelButton.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
                viewModel.searchTracks(query)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.cancelButton.setOnClickListener {
            binding.searchInputEditText.text.clear()
            viewModel.loadDownloadedTracks()
        }
    }

    private fun openPlayer(trackId: String) {
        sharedViewModel.setCurrentTrack(trackId)
        findNavController().navigate(Uri.parse("$DEEP_LINK$trackId"))
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus ?: View(requireContext())
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val DEEP_LINK = "musicavito://player/"
    }
}
