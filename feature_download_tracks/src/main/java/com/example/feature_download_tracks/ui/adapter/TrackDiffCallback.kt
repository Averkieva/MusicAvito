package com.example.feature_download_tracks.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.feature_playback_tracks.domain.model.Track

class TrackDiffCallback : DiffUtil.ItemCallback<Track>() {
    override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean = oldItem == newItem
}