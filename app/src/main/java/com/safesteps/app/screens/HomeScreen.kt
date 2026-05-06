package com.safesteps.app.screens

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.safesteps.app.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.safesteps.app.data.Contact
import com.safesteps.app.data.ContactsRepository
import com.safesteps.app.ui.components.SafeStepsCard
import com.safesteps.app.ui.components.SafeStepsScreenTitle
import com.safesteps.app.ui.components.StatusPill
import com.safesteps.app.utils.AnimationConstants
import com.safesteps.app.utils.AppConstants
import com.safesteps.app.utils.LocationConstants
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val repository = remember { ContactsRepository(context.applicationContext) }
    val contacts by repository.contacts.collectAsState(initial = emptyList())
    var isAlertTriggered by remember { mutableStateOf(false) }
    var showSosDialog by remember { mutableStateOf(false) }
    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )

    val scale by animateFloatAsState(
        targetValue = if (isAlertTriggered) {
            AnimationConstants.SosPressedScale
        } else {
            AnimationConstants.SosDefaultScale
        },
        label = "sos_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(dimensionResource(id = R.dimen.screen_horizontal_padding)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        HeaderSection(
            contactCount = contacts.size,
            isLocationGranted = locationPermissionState.status.isGranted
        )

        // SOS Button
        SosButtonSection(
            scale = scale,
            onSosClick = {
                isAlertTriggered = true
                showSosDialog = true
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    isAlertTriggered = false
                }, AnimationConstants.SosResetDelayMillis)
            }
        )

        // Quick Actions Card
        QuickActionsSection(
            context = context,
            isLocationGranted = locationPermissionState.status.isGranted,
            onRequestLocationPermission = { locationPermissionState.launchPermissionRequest() }
        )

        if (showSosDialog) {
            SosConfirmationDialog(
                onDismiss = { showSosDialog = false },
                onConfirm = {
                    showSosDialog = false
                    triggerSOSAlert(
                        context = context,
                        contacts = contacts,
                        hasLocationPermission = locationPermissionState.status.isGranted
                    )
                }
            )
        }
    }
}

@Composable
private fun ColumnScope.HeaderSection(
    contactCount: Int,
    isLocationGranted: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = dimensionResource(id = R.dimen.home_header_top_padding))
    ) {
        SafeStepsScreenTitle(titleRes = R.string.app_name)
        Text(
            text = stringResource(id = R.string.tagline),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = dimensionResource(id = R.dimen.spacing_small))
        )
        SafeStepsCard(
            modifier = Modifier.padding(top = dimensionResource(id = R.dimen.spacing_large)),
            elevation = dimensionResource(id = R.dimen.contact_card_elevation)
        ) {
            Text(
                text = stringResource(id = R.string.home_status_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(id = R.string.home_status_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = dimensionResource(id = R.dimen.spacing_xsmall))
            )
            Row(
                modifier = Modifier.padding(top = dimensionResource(id = R.dimen.spacing_medium)),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_small))
            ) {
                StatusPill(
                    text = if (isLocationGranted) {
                        stringResource(id = R.string.home_location_ready)
                    } else {
                        stringResource(id = R.string.home_location_needed)
                    },
                    color = if (isLocationGranted) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
                StatusPill(
                    text = stringResource(id = R.string.home_contacts_count, contactCount),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.SosButtonSection(scale: Float, onSosClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.sos_outer_ring_size))
                .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.32f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.sos_middle_ring_size))
                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.10f), CircleShape)
        )
        Button(
            onClick = onSosClick,
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.sos_button_size))
                .scale(scale),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = dimensionResource(id = R.dimen.sos_default_elevation),
                pressedElevation = dimensionResource(id = R.dimen.sos_pressed_elevation)
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = stringResource(id = R.string.sos_button),
                    modifier = Modifier.size(dimensionResource(id = R.dimen.sos_icon_size))
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))
                Text(
                    text = stringResource(id = R.string.sos_button),
                    fontSize = dimensionResource(id = R.dimen.sos_label_font_size).value.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = stringResource(id = R.string.sos_tap_hint),
                    fontSize = dimensionResource(id = R.dimen.sos_hint_font_size).value.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.QuickActionsSection(
    context: Context,
    isLocationGranted: Boolean,
    onRequestLocationPermission: () -> Unit
) {
    SafeStepsCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = dimensionResource(id = R.dimen.bottom_card_padding))
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.home_emergency_panel),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.spacing_xsmall))
            )
            Text(
                text = stringResource(id = R.string.home_emergency_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.spacing_medium))
            )

            Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_small))) {
                Button(
                    onClick = { callEmergencyServices(context) },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(id = R.dimen.primary_icon_size))
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_small)))
                    Text(stringResource(id = R.string.home_call_112))
                }

                FilledTonalButton(
                    onClick = {
                        if (isLocationGranted) {
                            shareLocation(context)
                        } else {
                            onRequestLocationPermission()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(id = R.dimen.primary_icon_size))
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_small)))
                    Text(
                        if (isLocationGranted)
                            stringResource(id = R.string.home_share_short)
                        else
                            stringResource(id = R.string.home_enable_location_short)
                    )
                }
            }
        }
    }
}

@Composable
private fun SosConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.sos_confirmation_title)) },
        text = { Text(text = stringResource(id = R.string.sos_confirmation_body)) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text = stringResource(id = R.string.sos_confirm_send))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.btn_cancel))
            }
        }
    )
}

private fun triggerSOSAlert(
    context: Context,
    contacts: List<Contact>,
    hasLocationPermission: Boolean
) {
    if (contacts.isEmpty()) {
        Toast.makeText(context, context.getString(R.string.sos_no_contacts), Toast.LENGTH_LONG).show()
        return
    }

    Toast.makeText(context, context.getString(R.string.sos_triggered), Toast.LENGTH_LONG).show()

    if (
        hasLocationPermission &&
        androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        LocationServices.getFusedLocationProviderClient(context).lastLocation
            .addOnSuccessListener { location ->
                val mapUrl = location?.let {
                    String.format(
                        Locale.US,
                        LocationConstants.GoogleMapsQueryUrl,
                        it.latitude,
                        it.longitude
                    )
                }
                openSosMessage(context, contacts, mapUrl)
            }
            .addOnFailureListener { openSosMessage(context, contacts, null) }
    } else {
        openSosMessage(context, contacts, null)
    }
}

private fun callEmergencyServices(context: Context) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:${AppConstants.EmergencyNumber}")
    }
    context.startActivity(intent)
}

private fun shareLocation(context: Context) {
    if (
        androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        LocationServices.getFusedLocationProviderClient(context).lastLocation
            .addOnSuccessListener { location ->
                val latitude = location?.latitude ?: LocationConstants.DefaultLocation.latitude
                val longitude = location?.longitude ?: LocationConstants.DefaultLocation.longitude
                openLocationShare(context, latitude, longitude)
            }
            .addOnFailureListener {
                openLocationShare(
                    context,
                    LocationConstants.DefaultLocation.latitude,
                    LocationConstants.DefaultLocation.longitude
                )
            }
    }
}

private fun openLocationShare(context: Context, latitude: Double, longitude: Double) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = AppConstants.PlainTextMimeType
        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_subject))
        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_body, latitude.toString(), longitude.toString()))
    }
    context.startActivity(
        Intent.createChooser(shareIntent, context.getString(R.string.share_location_chooser))
    )
}

private fun openSosMessage(context: Context, contacts: List<Contact>, mapUrl: String?) {
    val message = if (mapUrl == null) {
        context.getString(R.string.sos_message_without_location)
    } else {
        context.getString(R.string.sos_message_with_location, mapUrl)
    }

    val phoneNumbers = contacts
        .sortedByDescending { it.isPrimary }
        .joinToString(separator = ";") { Uri.encode(it.phoneNumber) }

    val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("smsto:$phoneNumbers")
        putExtra("sms_body", message)
    }

    try {
        context.startActivity(smsIntent)
        Toast.makeText(context, context.getString(R.string.alert_ready), Toast.LENGTH_SHORT).show()
    } catch (exception: ActivityNotFoundException) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = AppConstants.PlainTextMimeType
            putExtra(Intent.EXTRA_TEXT, message)
        }
        try {
            context.startActivity(
                Intent.createChooser(shareIntent, context.getString(R.string.sos_confirm_send))
            )
        } catch (fallbackException: ActivityNotFoundException) {
            Toast.makeText(context, context.getString(R.string.alert_app_missing), Toast.LENGTH_LONG).show()
        }
    }
}
