package com.example.core.data

import android.content.Context
import android.util.Log
import com.example.core.R
import com.example.core.domain.model.Track
import com.example.core.ui.viewmodel.SharedTrackViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class TrackDownloader(
    private val context: Context,
    private val sharedViewModel: SharedTrackViewModel
) {
    private val client = OkHttpClient()

    fun downloadTrack(track: Track, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder().url(track.preview).build()
                val response = client.newCall(request).execute()

                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        onError(Exception(context.getString(R.string.download_error, response.code())))
                    }
                    return@launch
                }

                val safeTitle = track.title.replace(Regex("[^a-zA-Z0-9_\\- ]"), "_")
                val file = File(context.getExternalFilesDir(null), "$safeTitle.mp3")

                file.parentFile?.let {
                    if (!it.exists()) {
                        if (it.mkdirs()) {
                            Log.d("TrackDownloader", "Directory crated: ${it.absolutePath}")
                        } else {
                            throw Exception(context.getString(R.string.create_directory_error))
                        }
                    }
                }

                val inputStream: InputStream = response.body()?.byteStream()
                    ?: throw Exception(context.getString(R.string.stream_read_error))

                FileOutputStream(file).use { output ->
                    inputStream.copyTo(output)
                }

                val downloadedTrack = track.copy(
                    preview = file.absolutePath,
                    album = track.album.copy(cover = track.album.cover),
                    artist = track.artist
                )
                sharedViewModel.addTrack(downloadedTrack)

                withContext(Dispatchers.Main) {
                    onSuccess()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            }
        }
    }
}