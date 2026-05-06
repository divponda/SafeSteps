package com.safesteps.app.alerts

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.safesteps.app.R
import com.safesteps.app.data.Contact
import com.safesteps.app.data.ContactsRepository
import com.safesteps.app.utils.LocationConstants
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

sealed class EmergencySmsResult {
    data class Sent(
        val totalContacts: Int,
        val sentCount: Int,
        val failedCount: Int,
        val skippedInvalidCount: Int
    ) : EmergencySmsResult()

    data object NoContacts : EmergencySmsResult()
    data object NoValidContacts : EmergencySmsResult()
    data object PermissionDenied : EmergencySmsResult()
}

class EmergencyAlertManager(private val context: Context) {

    private val appContext = context.applicationContext
    private val contactsRepository = ContactsRepository(appContext)

    suspend fun sendTimerExpiredSmsAlerts(): EmergencySmsResult {
        if (
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.SEND_SMS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return EmergencySmsResult.PermissionDenied
        }

        val contacts = contactsRepository.contacts.first()
        if (contacts.isEmpty()) {
            return EmergencySmsResult.NoContacts
        }

        val validContacts = contacts
            .filter { it.phoneNumber.any(Char::isDigit) }
            .sortedByDescending { it.isPrimary }
        if (validContacts.isEmpty()) {
            return EmergencySmsResult.NoValidContacts
        }

        val message = buildTimerExpiredMessage(getBestAvailableLocation())
        val smsManager = getSmsManager()
        var sentCount = 0
        var failedCount = 0

        validContacts.forEach { contact ->
            try {
                sendSms(smsManager, contact.phoneNumber.trim(), message)
                sentCount += 1
            } catch (exception: RuntimeException) {
                failedCount += 1
            } catch (exception: IllegalArgumentException) {
                failedCount += 1
            }
        }

        return EmergencySmsResult.Sent(
            totalContacts = contacts.size,
            sentCount = sentCount,
            failedCount = failedCount,
            skippedInvalidCount = contacts.size - validContacts.size
        )
    }

    private fun getSmsManager(): SmsManager {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            appContext.getSystemService(SmsManager::class.java)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getDefault()
        }
    }

    private fun sendSms(smsManager: SmsManager, phoneNumber: String, message: String) {
        val messageParts = smsManager.divideMessage(message)
        if (messageParts.size > SINGLE_SMS_PART_COUNT) {
            smsManager.sendMultipartTextMessage(phoneNumber, null, messageParts, null, null)
        } else {
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        }
    }

    private suspend fun getBestAvailableLocation(): LatLng? {
        if (
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        return try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)
            val cancellationTokenSource = CancellationTokenSource()
            val currentLocation = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).awaitOrNull()

            val location = currentLocation ?: fusedLocationClient.lastLocation.awaitOrNull()
            location?.toLatLng()
        } catch (exception: SecurityException) {
            null
        }
    }

    private fun buildTimerExpiredMessage(location: LatLng?): String {
        if (location == null) {
            return appContext.getString(R.string.timer_emergency_message_without_location)
        }

        val mapUrl = String.format(
            Locale.US,
            LocationConstants.GoogleMapsQueryUrl,
            location.latitude,
            location.longitude
        )
        return appContext.getString(R.string.timer_emergency_message_with_location, mapUrl)
    }

    private fun Location.toLatLng(): LatLng {
        return LatLng(latitude, longitude)
    }

    private suspend fun <T> Task<T>.awaitOrNull(): T? {
        return suspendCancellableCoroutine { continuation ->
            addOnSuccessListener { result ->
                if (continuation.isActive) continuation.resume(result)
            }
            addOnFailureListener {
                if (continuation.isActive) continuation.resume(null)
            }
            addOnCanceledListener {
                if (continuation.isActive) continuation.resume(null)
            }
        }
    }

    private companion object {
        const val SINGLE_SMS_PART_COUNT = 1
    }
}
