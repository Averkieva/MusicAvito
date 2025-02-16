package com.example.feature_download_tracks.ui.adapter

import android.view.ViewGroup
import com.example.core.ui.adapter.BaseTrackAdapter
import com.example.feature_download_tracks.ui.viewholder.TrackViewHolder

class DownloadedTracksAdapter(
    onTrackClick: (String) -> Unit
) : BaseTrackAdapter<TrackViewHolder>(onTrackClick) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = createTrackBinding(parent)
        return TrackViewHolder(binding, onTrackClick)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }
}