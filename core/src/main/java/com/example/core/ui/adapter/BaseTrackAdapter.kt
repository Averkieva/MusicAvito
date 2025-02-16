package com.example.core.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.core.databinding.ItemTrackBinding
import com.example.core.domain.model.Track

abstract class BaseTrackAdapter<VH : RecyclerView.ViewHolder>(
    val onTrackClick: (String) -> Unit
) : RecyclerView.Adapter<VH>() {

    protected var tracks: List<Track> = emptyList()

    override fun getItemCount(): Int = tracks.size

    fun updateTracks(newTracks: List<Track>) {
        val diffCallback = TrackDiffCallback(tracks, newTracks)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        tracks = newTracks
        diffResult.dispatchUpdatesTo(this)
    }

    protected fun createTrackBinding(parent: ViewGroup): ItemTrackBinding {
        return ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }
}
