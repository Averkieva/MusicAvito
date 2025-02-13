package com.example.feature_api_tracks.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.feature_api_tracks.databinding.ItemTrackBinding
import com.example.feature_api_tracks.ui.search.viewholder.ApiTrackViewHolder
import com.example.feature_playback_tracks.domain.model.Track

class ApiTrackAdapter(
    private var tracks: List<Track>,
    private val onTrackClick: (String) -> Unit
) : RecyclerView.Adapter<ApiTrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApiTrackViewHolder {
        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ApiTrackViewHolder(binding, onTrackClick)
    }

    override fun onBindViewHolder(holder: ApiTrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

    fun updateTracks(newTracks: List<Track>) {
        val diffCallback = TrackDiffCallback(tracks, newTracks)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        tracks = newTracks
        diffResult.dispatchUpdatesTo(this)
    }
}
