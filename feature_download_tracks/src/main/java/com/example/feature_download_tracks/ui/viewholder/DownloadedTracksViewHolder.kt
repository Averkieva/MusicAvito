package com.example.feature_download_tracks.ui.viewholder

import com.example.core.databinding.ItemTrackBinding
import com.example.core.ui.viewholder.BaseTrackViewHolder

class TrackViewHolder(
    binding: ItemTrackBinding,
    onTrackClick: (String) -> Unit
) : BaseTrackViewHolder(binding, onTrackClick)
