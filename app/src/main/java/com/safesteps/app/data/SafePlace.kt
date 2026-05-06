package com.safesteps.app.data

import com.google.android.gms.maps.model.LatLng

data class SafePlace(
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val address: String = ""
) {
    val location: LatLng get() = LatLng(latitude, longitude)
}
