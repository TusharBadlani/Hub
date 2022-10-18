package cessini.technology.commonui.utils.networkutil


import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.getSystemService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

const val TAG = "ConnectivityStateFlow"

/**
 * Listens for network callbacks using connectivity manager and returns a flow
 * which emits true when internet is available and false when not
 *
 * @param context the context
 * @return A cold flow which emits boolean values indicating internet connectivity
 */
fun connectivityStateFlow(context: Context): Flow<Boolean> = callbackFlow {
    val connectivityManager: ConnectivityManager = context.getSystemService()
        ?: error("Unable to get connectivity manager service")

    val validNetworks: MutableSet<Network> = HashSet()

    val callbacks = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            validNetworks.add(network)
            this@callbackFlow.trySend(true)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            validNetworks.remove(network)
            this@callbackFlow.trySend(validNetworks.isNotEmpty())
        }
    }

    val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    // Check the internet availability from currently active network before registering callback
    // This produces an initial value before a NetworkCallback is triggered
    val activeNetworkHasInternet = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false

    trySend(activeNetworkHasInternet)

    connectivityManager.registerNetworkCallback(networkRequest, callbacks)

    awaitClose {
        connectivityManager.unregisterNetworkCallback(callbacks)
    }
}