package com.safesteps.app.utils

import com.google.android.gms.maps.model.LatLng

object AppConstants {
    const val EmergencyNumber = "112"
    const val PlainTextMimeType = "text/plain"
}

object AnimationConstants {
    const val SosResetDelayMillis = 200L
    const val SosPressedScale = 0.95f
    const val SosDefaultScale = 1f
}

object ContactStorageConstants {
    const val PreferencesName = "contacts_preferences"
    const val EmergencyContactsKey = "emergency_contacts"
    const val EmptyContactsJson = "[]"
}

object SafePlacesStorageConstants {
    const val SafePlacesKey = "safe_places"
}

object LocationConstants {
    val DefaultLocation = LatLng(40.3289, -3.7638)           // UC3M Leganés campus
    val PoliceStationLocation = LatLng(40.3283, -3.7692)     // Comisaría de Leganés
    val HospitalLocation = LatLng(40.3274, -3.7595)          // Hospital Severo Ochoa
    val PharmacyLocation = LatLng(40.3296, -3.7621)          // Pharmacy near campus
    val UniversitySecurityLocation = LatLng(40.3289, -3.7638) // UC3M Security Office
    val PublicLibraryLocation = LatLng(40.3312, -3.7657)     // Biblioteca Municipal de Leganés
    val FireStationLocation = LatLng(40.3251, -3.7703)       // Parque de Bomberos de Leganés
    val WellLitAreaLocation = LatLng(40.3308, -3.7648)       // Plaza de España, Leganés
    const val DefaultMapZoom = 15f
    const val MAX_SAFE_PLACES_TO_DISPLAY = 8
    const val GoogleMapsQueryUrl = "https://maps.google.com/?q=%1$.6f,%2$.6f"
}

object TimerConstants {
    const val DefaultTimerDurationMinutes = 30
    const val MinimumTimerDurationMinutes = 1
    const val MaximumTimerDurationMinutes = 60
    const val SliderSteps = MaximumTimerDurationMinutes - MinimumTimerDurationMinutes - 1
    const val MaxRecentDurations = 5
    const val CountdownTickMillis = 1_000L
    const val SecondsPerMinute = 60
}

object NotificationConstants {
    const val SafetyTimerChannelId = "safety_timer_alerts"
    const val SafetyTimerExpiredNotificationId = 1001
}
