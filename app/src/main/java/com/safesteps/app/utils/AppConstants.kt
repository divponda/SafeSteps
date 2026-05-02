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

object LocationConstants {
    val DefaultLocation = LatLng(40.4168, -3.7038)
    val PoliceStationLocation = LatLng(40.4168, -3.7038)
    val HospitalLocation = LatLng(40.4180, -3.7060)
    val PharmacyLocation = LatLng(40.4148, -3.7012)
    val UniversitySecurityLocation = LatLng(40.4230, -3.7074)
    val PublicLibraryLocation = LatLng(40.4175, -3.7102)
    val FireStationLocation = LatLng(40.4129, -3.7078)
    val WellLitAreaLocation = LatLng(40.4154, -3.7070)
    const val DefaultMapZoom = 14f
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
