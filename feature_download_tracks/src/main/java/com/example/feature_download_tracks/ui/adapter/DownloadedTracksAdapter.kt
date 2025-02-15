package com.example.feature_download_tracks.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.feature_download_tracks.databinding.ItemTrackBinding
import com.example.feature_download_tracks.ui.viewholder.TrackViewHolder
import com.example.feature_playback_tracks.domain.model.Track

class DownloadedTracksAdapter(
    private val onTrackClick: (String) -> Unit
) : ListAdapter<Track, TrackViewHolder>(TrackDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding, onTrackClick)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}