package com.example.core.data.mapper

import com.example.core.data.model.PlayerTrackResponse
import com.example.core.domain.model.Album
import com.example.core.domain.model.Artist
import com.example.core.domain.model.Track
import com.example.core.utils.TimeAndDateUtils.formatReleaseDate

/**
 * Объект `TrackMapper` используется для преобразования `PlayerTrackResponse` в `Track`
 */
object TrackMapper {

    /**
     * Преобразует `PlayerTrackResponse` в `Track`.
     *
     * @param response Объект `PlayerTrackResponse`, содержащий информацию о треке из API.
     * @return Объект `Track`, который используется в доменном и UI слоях.
     */
    fun mapTrack(response: PlayerTrackResponse): Track {
        return Track(
            id = response.id,
            title = response.title,
            artist = Artist(
                id = response.artist.id,
                name = response.artist.name
            ),
            album = Album(
                id = response.album.id,
                title = response.album.title,
                cover = response.album.cover,
                releaseDate = formatReleaseDate(response.album.releaseDate)
            ),
            duration = response.duration,
            preview = response.preview,
            trackPosition = response.trackPosition
        )
    }
}

