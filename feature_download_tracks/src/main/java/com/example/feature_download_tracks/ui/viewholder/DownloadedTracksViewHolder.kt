package com.example.feature_download_tracks.ui.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.feature_download_tracks.databinding.ItemTrackBinding
import com.example.feature_playback_tracks.domain.model.Track
import com.example.feature_playback_tracks.utils.TimeAndDateUtils

class TrackViewHolder(
    private val binding: ItemTrackBinding,
    private val onTrackClick: (String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        binding.trackTitleTextView.text = track.title
        val formattedDuration = TimeAndDateUtils.formatDuration(track.duration)
        val artistWithDuration = "${track.artist.name} • $formattedDuration"
        binding.trackArtistTextView.text = artistWithDuration

        Glide.with(binding.root)
            .load(track.album.cover)
            .placeholder(com.example.feature_playback_tracks.R.drawable.cover_placeholder)
            .error(com.example.feature_playback_tracks.R.drawable.cover_placeholder)
            .transform(CenterCrop(), RoundedCorners(10))
            .into(binding.trackCoverImageView)

        binding.root.setOnClickListener {
            onTrackClick(track.id)
        }
    }
}