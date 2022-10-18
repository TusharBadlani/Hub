package cessini.technology.newapi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@SuppressLint("MissingPermission")
@Singleton
class NetworkState @Inject constructor(
    @ApplicationContext context: Context,
) : ConnectivityManager.NetworkCallback() {
    private val _connected = MutableStateFlow(value = false)
    val connected: StateFlow<Boolean> = _connected

    init {
        run { context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager }
            .registerNetworkCallback(
                NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build(),
                this
            )
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)

        _connected.value = true
    }

    override fun onUnavailable() {
        super.onUnavailable()

        _connected.value = false
    }

    override fun onLost(network: Network) {
        super.onLost(network)

        _connected.value = false
    }

}
