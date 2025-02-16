package com.example.feature_api_tracks.data.repository

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import retrofit2.HttpException
import java.net.UnknownHostException

object ApiTrackErrorHandler {
    sealed class TrackError(@StringRes val messageRes: Int, @DrawableRes val imageRes: Int) : Throwable() {
        data object NoResults : TrackError(com.example.core.R.string.track_error_no_result, com.example.core.R.drawable.no_results_error_image) {
            private fun readResolve(): Any = NoResults
        }

        data object NoInternet : TrackError(com.example.core.R.string.track_error_no_internet, com.example.core.R.drawable.no_internet_error_image) {
            private fun readResolve(): Any = NoInternet
        }

        data object ServerError : TrackError(com.example.core.R.string.track_error_server_error, com.example.core.R.drawable.server_error_image) {
            private fun readResolve(): Any = ServerError
        }

        data object UnknownError : TrackError(com.example.core.R.string.track_error_unknown_error, com.example.core.R.drawable.unknown_error_image) {
            private fun readResolve(): Any = UnknownError
        }
    }

    fun getError(e: Exception): TrackError {
        return when (e) {
            is UnknownHostException -> TrackError.NoInternet
            is HttpException -> TrackError.ServerError
            else -> TrackError.UnknownError
        }
    }

    fun getEmptyStateError(): TrackError {
        return TrackError.NoResults
    }
}


