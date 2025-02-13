package com.example.feature_api_tracks.ui.search.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.feature_api_tracks.databinding.ItemTrackBinding
import com.example.feature_api_tracks.domain.search.model.Track
import com.example.feature_api_tracks.ui.search.viewholder.ApiTrackViewHolder

class ApiTrackAdapter(private var tracks: List<Track>) :
    RecyclerView.Adapter<ApiTrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApiTrackViewHolder {
        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ApiTrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ApiTrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }
}
