package com.safesteps.app.data

import com.google.android.gms.maps.model.LatLng

object Constants {
    // Default Map Settings
    val DEFAULT_LOCATION = LatLng(40.4168, -3.7038) // Madrid
    const val DEFAULT_ZOOM = 14f
    
    // Emergency Numbers
    const val EMERGENCY_NUMBER = "112"
    
    // Timer Defaults
    const val DEFAULT_TIMER_MINUTES = 30
    const val MAX_TIMER_MINUTES = 120
}