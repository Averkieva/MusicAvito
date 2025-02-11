package com.example.feature_api_tracks.ui.search.view_holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.feature_api_tracks.R
import com.example.feature_api_tracks.domain.search.model.Track

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackTitle: TextView = itemView.findViewById(R.id.track_title)
    private val trackArtist: TextView = itemView.findViewById(R.id.track_artist)
    private val trackCover: ImageView = itemView.findViewById(R.id.track_cover)

    fun bind(track: Track) {
        trackTitle.text = track.title
        trackArtist.text = track.artist
        Glide.with(itemView.context)
            .load(track.coverUrl)
            .placeholder(R.drawable.cover_placeholder)
            .into(trackCover)
    }
}
