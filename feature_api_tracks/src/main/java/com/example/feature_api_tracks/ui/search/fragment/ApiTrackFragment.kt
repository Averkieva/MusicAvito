package com.example.feature_api_tracks.ui.search.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.feature_api_tracks.data.api.RetrofitClient
import com.example.feature_api_tracks.data.repository.ApiTrackRepositoryImpl
import com.example.feature_api_tracks.databinding.FragmentApiTracksBinding
import com.example.feature_api_tracks.ui.search.adapter.ApiTrackAdapter
import com.example.feature_api_tracks.ui.search.viewmodel.ApiTrackViewModel
import com.example.feature_api_tracks.ui.search.viewmodel.ApiViewModelFactory
import com.example.feature_api_tracks.utils.NetworkObserver

class ApiTrackFragment : Fragment() {
    private var _binding: FragmentApiTracksBinding? = null
    private val binding get() = _binding!!

    private val networkObserver by lazy { NetworkObserver(requireContext()) }
    private val trackAdapter by lazy { ApiTrackAdapter(emptyList()) }
    private val viewModel by lazy {
        val repository = ApiTrackRepositoryImpl(RetrofitClient.api)
        ViewModelProvider(this, ApiViewModelFactory(repository))[ApiTrackViewModel::class.java]
    }

    private var currentQuery: String? = null

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

        setupRecyclerView()
        setupObservers()
        setupSearch()
        observeNetworkChanges()

        viewModel.loadTopTracks()

        binding.root.setOnTouchListener { _, _ ->
            hideKeyboard()
            binding.searchInputEditText.clearFocus()
            false
        }
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
            binding.errorLayout.visibility = View.GONE
            binding.resultRecyclerView.visibility = View.VISIBLE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            val hasError = error != null
            if (hasError) {
                binding.errorLayout.visibility = View.VISIBLE
                binding.resultRecyclerView.visibility = View.GONE
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
                        if (query.isNotEmpty()) View.VISIBLE else View.GONE

                    currentQuery = query

                    if (query.isNotEmpty()) viewModel.searchTracks(query)
                    else viewModel.loadTopTracks()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.cancelButton.setOnClickListener {
            binding.searchInputEditText.text.clear()
            currentQuery = null
        }

        binding.cancelButton.visibility = View.GONE
    }

    private fun observeNetworkChanges() {
        networkObserver.networkAvailable.observe(viewLifecycleOwner) { isAvailable ->
            if (isAvailable) {
                if (currentQuery.isNullOrEmpty()) {
                    viewModel.loadTopTracks()
                } else {
                    viewModel.searchTracks(currentQuery!!)
                }
            }
        }
        networkObserver.register()
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        networkObserver.unregister()
        _binding = null
    }
}
