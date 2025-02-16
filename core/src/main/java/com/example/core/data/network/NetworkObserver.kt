package com.example.core.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * `NetworkObserver` отслеживает изменения в состоянии сети.
 * Использует `ConnectivityManager` для подписки на события подключения и отключения сети.
 *
 * @param context Контекст приложения, необходимый для получения `ConnectivityManager`.
 */
class NetworkObserver(context: Context) {

    // Менеджер подключения для отслеживания состояния сети
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _networkAvailable = MutableLiveData<Boolean>()
    val networkAvailable: LiveData<Boolean> get() = _networkAvailable

    /**
     * Колбэк для обработки изменений в сети.
     * - `onAvailable` вызывается, когда сеть становится доступной.
     * - `onLost` вызывается, когда соединение с сетью теряется.
     */
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            _networkAvailable.postValue(true)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            _networkAvailable.postValue(false)
        }
    }

    /**
     * Регистрирует `networkCallback` для получения обновлений состояния сети.
     * Вызывается при старте компонента, который отслеживает сеть.
     */
    fun register() {
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    /**
     * Отменяет регистрацию `networkCallback` и перестает отслеживать сеть.
     * Вызывается, когда компонент уничтожается, чтобы избежать утечек памяти.
     */
    fun unregister() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}