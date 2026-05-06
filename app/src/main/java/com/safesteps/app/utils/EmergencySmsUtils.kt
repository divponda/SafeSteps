package com.safesteps.app.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.safesteps.app.R
import com.safesteps.app.data.Contact
import java.util.Locale

fun triggerEmergencySmsWithLocation(
    context: Context,
    contacts: List<Contact>,
    fallbackMessageResId: Int = R.string.sos_message_without_location,
    locationMessageResId: Int = R.string.sos_message_with_location
) {
    if (contacts.isEmpty()) {
        Toast.makeText(
            context,
            context.getString(R.string.sos_no_contacts),
            Toast.LENGTH_LONG
        ).show()
        return
    }

    val hasFineLocationPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (hasFineLocationPermission || hasCoarseLocationPermission) {
        openEmergencySmsAfterLocationLookup(
            context = context,
            contacts = contacts,
            fallbackMessageResId = fallbackMessageResId,
            locationMessageResId = locationMessageResId
        )
    } else {
        openEmergencySms(
            context = context,
            contacts = contacts,
            mapUrl = null,
            fallbackMessageResId = fallbackMessageResId,
            locationMessageResId = locationMessageResId
        )
    }
}

@SuppressLint("MissingPermission")
private fun openEmergencySmsAfterLocationLookup(
    context: Context,
    contacts: List<Contact>,
    fallbackMessageResId: Int,
    locationMessageResId: Int
) {
    LocationServices.getFusedLocationProviderClient(context)
        .lastLocation
        .addOnSuccessListener { location ->
            val mapUrl = location?.let {
                String.format(
                    Locale.US,
                    LocationConstants.GoogleMapsQueryUrl,
                    it.latitude,
                    it.longitude
                )
            }

            openEmergencySms(
                context = context,
                contacts = contacts,
                mapUrl = mapUrl,
                fallbackMessageResId = fallbackMessageResId,
                locationMessageResId = locationMessageResId
            )
        }
        .addOnFailureListener {
            openEmergencySms(
                context = context,
                contacts = contacts,
                mapUrl = null,
                fallbackMessageResId = fallbackMessageResId,
                locationMessageResId = locationMessageResId
            )
        }
}

private fun openEmergencySms(
    context: Context,
    contacts: List<Contact>,
    mapUrl: String?,
    fallbackMessageResId: Int,
    locationMessageResId: Int
) {
    val message = if (mapUrl == null) {
        context.getString(fallbackMessageResId)
    } else {
        context.getString(locationMessageResId, mapUrl)
    }

    val phoneNumbers = contacts
        .sortedByDescending { it.isPrimary }
        .joinToString(separator = ";") {
            Uri.encode(it.phoneNumber)
        }

    val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("smsto:$phoneNumbers")
        putExtra("sms_body", message)
    }

    try {
        context.startActivity(smsIntent)
        Toast.makeText(context, context.getString(R.string.alert_ready), Toast.LENGTH_SHORT).show()
    } catch (e: ActivityNotFoundException) {
        // Fallback to generic share if smsto fails or no SMS app found
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = AppConstants.PlainTextMimeType
            putExtra(Intent.EXTRA_TEXT, message)
        }
        try {
            context.startActivity(
                Intent.createChooser(shareIntent, context.getString(R.string.sos_confirm_send))
            )
        } catch (fallbackException: ActivityNotFoundException) {
            Toast.makeText(
                context,
                context.getString(R.string.alert_app_missing),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
