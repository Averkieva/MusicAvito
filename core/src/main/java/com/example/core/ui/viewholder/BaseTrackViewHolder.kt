package com.example.core.ui.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.core.utils.TimeAndDateUtils
import com.example.core.R
import com.example.core.domain.model.Track
import com.example.core.databinding.ItemTrackBinding

abstract class BaseTrackViewHolder(
    private val binding: ItemTrackBinding,
    private val onTrackClick: (String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    open fun bind(track: Track) {
        binding.trackTitleTextView.text = track.title
        val formattedDuration = TimeAndDateUtils.formatDuration(track.duration)
        val artistWithDuration = "${track.artist.name} • $formattedDuration"
        binding.trackArtistTextView.text = artistWithDuration

        Glide.with(binding.root)
            .load(track.album.cover)
            .placeholder(R.drawable.cover_placeholder)
            .error(R.drawable.cover_placeholder)
            .transform(CenterCrop(), RoundedCorners(10))
            .into(binding.trackCoverImageView)

        binding.root.setOnClickListener {
            onTrackClick(track.id)
        }
    }
}
