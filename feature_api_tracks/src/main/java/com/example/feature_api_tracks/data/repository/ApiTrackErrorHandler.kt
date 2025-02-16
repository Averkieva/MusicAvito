package com.example.feature_api_tracks.data.repository

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import retrofit2.HttpException
import java.net.UnknownHostException

/**
 * Объект `ApiTrackErrorHandler` предназначен для обработки ошибок, возникающих при запросах к API.
 * Он конвертирует исключения в пользовательские ошибки `TrackError`, содержащие сообщения и изображения,
 * которые могут быть использованы в UI для отображения пользователю.
 */
object ApiTrackErrorHandler {

    /**
     * Базовый класс ошибок `TrackError`, используемый для отображения сообщений и изображений в UI.
     * Наследует `Throwable`, чтобы можно было использовать его в `Result.failure()`.
     *
     * @param messageRes ID строкового ресурса с описанием ошибки.
     * @param imageRes ID ресурса изображения, связанного с ошибкой.
     */
    sealed class TrackError(@StringRes val messageRes: Int, @DrawableRes val imageRes: Int) :
        Throwable() {

        /** Ошибка: Нет результатов (например, при поиске) */
        data object NoResults : TrackError(
            com.example.core.R.string.track_error_no_result,
            com.example.core.R.drawable.no_results_error_image
        ) {
            private fun readResolve(): Any = NoResults
        }

        /** Ошибка: Нет подключения к интернету */
        data object NoInternet : TrackError(
            com.example.core.R.string.track_error_no_internet,
            com.example.core.R.drawable.no_internet_error_image
        ) {
            private fun readResolve(): Any = NoInternet
        }

        /** Ошибка: Проблема на стороне сервера */
        data object ServerError : TrackError(
            com.example.core.R.string.track_error_server_error,
            com.example.core.R.drawable.server_error_image
        ) {
            private fun readResolve(): Any = ServerError
        }

        /** Ошибка: Неизвестная ошибка */
        data object UnknownError : TrackError(
            com.example.core.R.string.track_error_unknown_error,
            com.example.core.R.drawable.unknown_error_image
        ) {
            private fun readResolve(): Any = UnknownError
        }
    }

    /**
     * Определяет тип ошибки на основе переданного исключения.
     *
     * @param e Исключение, которое возникло при выполнении запроса.
     * @return Соответствующий объект `TrackError`.
     */
    fun getError(e: Exception): TrackError {
        return when (e) {
            is UnknownHostException -> TrackError.NoInternet
            is HttpException -> TrackError.ServerError
            else -> TrackError.UnknownError
        }
    }

    /**
     * Возвращает ошибку пустого состояния (например, если сервер не вернул треки).
     *
     * @return Объект `TrackError.NoResults`
     */
    fun getEmptyStateError(): TrackError {
        return TrackError.NoResults
    }
}


