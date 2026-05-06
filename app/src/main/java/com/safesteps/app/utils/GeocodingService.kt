package com.safesteps.app.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.safesteps.app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

// Geocoding converts humanreadable address into geographic coordinates (lat/lng)
// Based on the  pattern from the class slides in unit 6
class GeocodingService(context: Context) {

    private val appContext = context.applicationContext
    private val geocoder = Geocoder(appContext, Locale.getDefault())

    // returns formatted "lat, lon" string
    suspend fun geocodeAddress(address: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            // two code paths depending on API level, as shown in slides
            val results = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine<List<Address>?> { cont ->
                    geocoder.getFromLocationName(address, 1) { list ->
                        if (cont.isActive) cont.resume(list)
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocationName(address, 1)
            }

            if (results.isNullOrEmpty()) {
                Result.failure(NoSuchElementException(appContext.getString(R.string.no_results)))
            } else {
                val location = results[0]
                Result.success(
                    appContext.getString(
                        R.string.lat_lon, location.latitude, location.longitude
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
