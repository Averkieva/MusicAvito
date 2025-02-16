package com.example.feature_api_tracks.ui.search.adapter

import android.view.ViewGroup
import com.example.core.ui.adapter.BaseTrackAdapter
import com.example.feature_api_tracks.ui.search.viewholder.ApiTrackViewHolder

class ApiTrackAdapter(
    onTrackClick: (String) -> Unit
) : BaseTrackAdapter<ApiTrackViewHolder>(onTrackClick) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApiTrackViewHolder {
        val binding = createTrackBinding(parent)
        return ApiTrackViewHolder(binding, onTrackClick)
    }

    override fun onBindViewHolder(holder: ApiTrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }
}

