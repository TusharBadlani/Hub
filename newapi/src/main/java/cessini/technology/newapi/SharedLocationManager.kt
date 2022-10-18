package cessini.technology.newapi

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import java.util.*

class SharedLocationManager constructor(
    context: Context,
    externalScope: CoroutineScope
) {

    private val geocode: Geocoder = Geocoder(context, Locale.getDefault())
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 1000
        numUpdates = 1
    }

    private val _locationUpdates = callbackFlow<Location> {
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                trySend(result.lastLocation)
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper()
        ).addOnFailureListener { e ->
            close(e)
        }

        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }

    }.shareIn(
        externalScope,
        replay = 0,
        started = SharingStarted.WhileSubscribed(5000)
    )

    fun getAddress(latitude: Double, longitutde: Double): String {
        val addressList: List<Address>? = geocode.getFromLocation(latitude, longitutde, 1)
        if (addressList.isNullOrEmpty()) {
            return ""
        }

        val address: Address = addressList[0]
        val stringBuilder = StringBuilder()
        for (i in 0..address.maxAddressLineIndex) {
            stringBuilder.append(address.getAddressLine(i)).append(" ")
        }
        stringBuilder.deleteCharAt(stringBuilder.length - 1)
        return stringBuilder.toString()
    }

    fun locationFlow(): Flow<Location> {
        return _locationUpdates
    }

}