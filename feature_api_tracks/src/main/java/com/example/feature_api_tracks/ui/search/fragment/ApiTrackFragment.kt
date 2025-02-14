package com.example.feature_api_tracks.ui.search.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.feature_api_tracks.databinding.FragmentApiTracksBinding
import com.example.feature_api_tracks.di.DaggerFeatureComponent
import com.example.feature_api_tracks.ui.search.adapter.ApiTrackAdapter
import com.example.feature_api_tracks.ui.search.viewmodel.ApiTrackViewModel
import com.example.feature_api_tracks.utils.NetworkObserver
import com.example.feature_playback_tracks.ui.player.viewmodel.SharedTrackViewModel
import javax.inject.Inject

class ApiTrackFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: ApiTrackViewModel

    private var _binding: FragmentApiTracksBinding? = null
    private val binding get() = _binding!!

    private val networkObserver by lazy { NetworkObserver(requireContext()) }

    private val trackAdapter by lazy {
        ApiTrackAdapter(emptyList()) { trackId ->
            openPlayer(trackId)
        }
    }

    private val sharedViewModel: SharedTrackViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val featureComponent = DaggerFeatureComponent.factory().create()
        featureComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApiTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory)[ApiTrackViewModel::class.java]

        setupRecyclerView()
        setupObservers()
        setupSearch()
        observeNetworkChanges()

        viewModel.restoreLastTracks()

        binding.root.setOnTouchListener { _, _ ->
            hideKeyboard()
            binding.searchInputEditText.clearFocus()
            false
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.restoreLastTracks()
    }

    private fun setupRecyclerView() {
        binding.resultRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = trackAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        hideKeyboard()
                    }
                }
            })
        }
    }

    private fun setupObservers() {
        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            trackAdapter.updateTracks(tracks)
            sharedViewModel.setTrackList(tracks)
            binding.errorLayout.visibility = GONE
            binding.resultRecyclerView.visibility = VISIBLE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            val hasError = error != null
            if (hasError) {
                binding.errorLayout.visibility = VISIBLE
                binding.resultRecyclerView.visibility = GONE
                if (error != null) {
                    binding.errorMessageTextView.text = getString(error.messageRes)
                }
                if (error != null) {
                    binding.errorImageView.setImageResource(error.imageRes)
                }
            }
        }
    }

    private fun setupSearch() {
        binding.searchInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.toString()?.let { query ->
                    binding.cancelButton.visibility =
                        if (query.isNotEmpty()) VISIBLE else GONE

                    if (query.isNotEmpty()) viewModel.searchTracks(query)
                    else viewModel.loadTopTracks()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.cancelButton.setOnClickListener {
            binding.searchInputEditText.text.clear()
        }

        binding.cancelButton.visibility = GONE
    }

    private fun observeNetworkChanges() {
        networkObserver.networkAvailable.observe(viewLifecycleOwner) { isAvailable ->
            if (isAvailable) {
                viewModel.restoreLastTracks()
            }
        }
        networkObserver.register()
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun openPlayer(trackId: String) {
        sharedViewModel.setCurrentTrack(trackId)
        findNavController().navigate(Uri.parse("$DEEP_LINK$trackId"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        networkObserver.unregister()
        _binding = null
    }

    companion object {
        private const val DEEP_LINK = "musicavito://player/"
    }
}
