package com.example.feature_api_tracks.ui.search.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.feature_api_tracks.R
import com.example.feature_api_tracks.databinding.ItemTrackBinding
import com.example.feature_api_tracks.domain.search.model.Track
import com.example.feature_api_tracks.utils.TimeUtils

class ApiTrackViewHolder(private val binding: ItemTrackBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        binding.trackTitleTextView.text = track.title

        val formattedDuration = TimeUtils.formatDuration(track.duration)
        val artistWithDuration = "${track.artist.name} • $formattedDuration"
        binding.trackArtistTextView.text = artistWithDuration

        Glide.with(binding.root)
            .load(track.album.cover)
            .placeholder(R.drawable.cover_placeholder)
            .error(R.drawable.cover_placeholder)
            .transform(CenterCrop(), RoundedCorners(10))
            .into(binding.trackCoverImageView)
    }
}