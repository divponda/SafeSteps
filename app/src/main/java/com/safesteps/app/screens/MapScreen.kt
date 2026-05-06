package com.safesteps.app.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import com.safesteps.app.R
import com.safesteps.app.data.SafePlace
import com.safesteps.app.data.SafePlacesRepository
import com.safesteps.app.ui.components.SafeStepsCard
import com.safesteps.app.utils.GeocodingService
import com.safesteps.app.utils.LocationConstants
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val repository = remember { SafePlacesRepository(context.applicationContext) }
    val scope = rememberCoroutineScope()
    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LocationConstants.DefaultLocation,
            LocationConstants.DefaultMapZoom
        )
    }

    var isMapLoaded by remember { mutableStateOf(false) }
    val isLocationGranted = locationPermissionState.status.isGranted
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var showSafePlacesDialog by remember { mutableStateOf(false) }
    val persistedPlaces by repository.safePlaces.collectAsState(initial = null)
    val defaultPlaces = rememberDefaultSafePlaces()
    var safePlaces by remember(persistedPlaces) {
        mutableStateOf(persistedPlaces ?: defaultPlaces)
    }

    LaunchedEffect(isLocationGranted) {
        if (
            isLocationGranted &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            LocationServices.getFusedLocationProviderClient(context).lastLocation
                .addOnSuccessListener { location ->
                    currentLocation = location?.let { LatLng(it.latitude, it.longitude) }
                }
        }
    }

    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = true,
                mapToolbarEnabled = true
            )
        )
    }

    val properties by remember(isLocationGranted) {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = isLocationGranted
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = properties,
            uiSettings = uiSettings,
            onMapLoaded = { isMapLoaded = true }
        ) {
            currentLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = stringResource(id = R.string.current_location),
                    snippet = stringResource(id = R.string.current_location_desc)
                )
            }

            safePlaces.forEach { safePlace ->
                Marker(
                    state = MarkerState(position = safePlace.location),
                    title = safePlace.title,
                    snippet = safePlace.description
                )
            }
        }

        SafeStepsCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.spacing_large))
                .align(Alignment.TopCenter)
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.safety_map_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = R.string.safety_map_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = dimensionResource(id = R.dimen.spacing_xsmall))
                )
                if (!isLocationGranted) {
                    Text(
                        text = stringResource(id = R.string.location_permission_message),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = dimensionResource(id = R.dimen.spacing_small))
                    )
                    Button(
                        onClick = { locationPermissionState.launchPermissionRequest() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimensionResource(id = R.dimen.spacing_small))
                    ) {
                        Text(text = stringResource(id = R.string.location_permission_button))
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showSafePlacesDialog = true },
            modifier = Modifier
                .padding(
                    start = dimensionResource(id = R.dimen.spacing_large),
                    bottom = dimensionResource(id = R.dimen.map_fab_bottom_padding)
                )
                .align(Alignment.BottomStart),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = stringResource(id = R.string.open_safe_places)
            )
        }

        if (!isMapLoaded) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                strokeWidth = dimensionResource(id = R.dimen.spacing_xsmall)
            )
        }

        if (showSafePlacesDialog) {
            SafePlacesDialog(
                safePlaces = safePlaces,
                onDismiss = { showSafePlacesDialog = false },
                onSafePlacesChanged = { safePlaces = it },
                onSave = {
                    // Use GeocodingService (from Unit 6 slides) to convert any typed address
                    // into real coordinates before saving, so the marker lands in the right place
                    val geocodingService = GeocodingService(context)
                    scope.launch {
                        val geocodedPlaces = safePlaces.map { place ->
                            if (place.address.isNotBlank()) {
                                geocodingService.geocodeAddress(place.address)
                                    .fold(
                                        onSuccess = { latLonString ->
                                            //returns lat,long as string
                                            // so we parse it back into separate coordinates here
                                            val parts = latLonString.split(", ")
                                            place.copy(
                                                latitude = parts[0].toDouble(),
                                                longitude = parts[1].toDouble()
                                            )
                                        },
                                        onFailure = {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.geocoding_failed),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            place
                                        }
                                    )
                            } else {
                                place
                            }
                        }
                        safePlaces = geocodedPlaces
                        repository.saveSafePlaces(geocodedPlaces)
                    }
                    showSafePlacesDialog = false
                }
            )
        }
    }
}

@Composable
private fun rememberDefaultSafePlaces(): List<SafePlace> {
    val policeStation = stringResource(id = R.string.police_station)
    val policeStationDesc = stringResource(id = R.string.police_station_desc)
    val hospital = stringResource(id = R.string.hospital)
    val hospitalDesc = stringResource(id = R.string.hospital_desc)
    val pharmacy = stringResource(id = R.string.pharmacy)
    val pharmacyDesc = stringResource(id = R.string.pharmacy_desc)
    val universitySecurity = stringResource(id = R.string.university_security)
    val universitySecurityDesc = stringResource(id = R.string.university_security_desc)
    val publicLibrary = stringResource(id = R.string.public_library)
    val publicLibraryDesc = stringResource(id = R.string.public_library_desc)
    val fireStation = stringResource(id = R.string.fire_station)
    val fireStationDesc = stringResource(id = R.string.fire_station_desc)
    val wellLitArea = stringResource(id = R.string.well_lit_area)
    val wellLitAreaDesc = stringResource(id = R.string.well_lit_area_desc)

    return remember {
        listOf(
            SafePlace(policeStation, policeStationDesc, LocationConstants.PoliceStationLocation.latitude, LocationConstants.PoliceStationLocation.longitude),
            SafePlace(hospital, hospitalDesc, LocationConstants.HospitalLocation.latitude, LocationConstants.HospitalLocation.longitude),
            SafePlace(pharmacy, pharmacyDesc, LocationConstants.PharmacyLocation.latitude, LocationConstants.PharmacyLocation.longitude),
            SafePlace(universitySecurity, universitySecurityDesc, LocationConstants.UniversitySecurityLocation.latitude, LocationConstants.UniversitySecurityLocation.longitude),
            SafePlace(publicLibrary, publicLibraryDesc, LocationConstants.PublicLibraryLocation.latitude, LocationConstants.PublicLibraryLocation.longitude),
            SafePlace(fireStation, fireStationDesc, LocationConstants.FireStationLocation.latitude, LocationConstants.FireStationLocation.longitude),
            SafePlace(wellLitArea, wellLitAreaDesc, LocationConstants.WellLitAreaLocation.latitude, LocationConstants.WellLitAreaLocation.longitude)
        )
    }
}

@Composable
private fun SafePlacesDialog(
    safePlaces: List<SafePlace>,
    onDismiss: () -> Unit,
    onSafePlacesChanged: (List<SafePlace>) -> Unit,
    onSave: () -> Unit
) {
    val newPlaceTitle = stringResource(id = R.string.new_safe_place)
    val newPlaceDescription = stringResource(id = R.string.new_safe_place_desc)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.nearby_safe_places)) },
        text = {
            Column {
                LazyColumn(
                    modifier = Modifier.heightIn(
                        max = dimensionResource(id = R.dimen.map_safe_places_max_height)
                    )
                ) {
                    itemsIndexed(safePlaces) { index, safePlace ->
                        SafePlaceEditorRow(
                            safePlace = safePlace,
                            canMoveUp = index > 0,
                            canMoveDown = index < safePlaces.lastIndex,
                            onTitleChanged = { title ->
                                onSafePlacesChanged(
                                    safePlaces.toMutableList().also {
                                        it[index] = safePlace.copy(title = title)
                                    }
                                )
                            },
                            onDescriptionChanged = { description ->
                                onSafePlacesChanged(
                                    safePlaces.toMutableList().also {
                                        it[index] = safePlace.copy(description = description)
                                    }
                                )
                            },
                            onAddressChanged = { address ->
                                onSafePlacesChanged(
                                    safePlaces.toMutableList().also {
                                        it[index] = safePlace.copy(address = address)
                                    }
                                )
                            },
                            onMoveUp = {
                                onSafePlacesChanged(safePlaces.moveItem(index, index - 1))
                            },
                            onMoveDown = {
                                onSafePlacesChanged(safePlaces.moveItem(index, index + 1))
                            },
                            onDelete = {
                                onSafePlacesChanged(safePlaces.filterIndexed { placeIndex, _ -> placeIndex != index })
                            }
                        )
                    }
                }

                TextButton(
                    onClick = {
                        onSafePlacesChanged(
                            safePlaces + SafePlace(
                                title = newPlaceTitle,
                                description = newPlaceDescription,
                                latitude = LocationConstants.DefaultLocation.latitude,
                                longitude = LocationConstants.DefaultLocation.longitude
                            )
                        )
                    },
                    modifier = Modifier.padding(top = dimensionResource(id = R.dimen.spacing_small))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    Text(text = stringResource(id = R.string.add_safe_place))
                }
            }
        },
        confirmButton = {
            Button(onClick = onSave) {
                Text(text = stringResource(id = R.string.save_safe_place))
            }
        }
    )
}

@Composable
private fun SafePlaceEditorRow(
    safePlace: SafePlace,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onAddressChanged: (String) -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dimensionResource(id = R.dimen.spacing_medium))
    ) {
        OutlinedTextField(
            value = safePlace.title,
            onValueChange = onTitleChanged,
            label = { Text(text = stringResource(id = R.string.safe_place_name)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = safePlace.description,
            onValueChange = onDescriptionChanged,
            label = { Text(text = stringResource(id = R.string.safe_place_note)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(id = R.dimen.spacing_small))
        )
        // Address field — when the user types an address and saves, the Geocoder (Unit 6)
        // converts it to coordinates so the marker appears at the right location on the map
        OutlinedTextField(
            value = safePlace.address,
            onValueChange = onAddressChanged,
            label = { Text(text = stringResource(id = R.string.safe_place_address)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimensionResource(id = R.dimen.spacing_small))
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onMoveUp,
                enabled = canMoveUp
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = stringResource(id = R.string.move_safe_place_up)
                )
            }
            IconButton(
                onClick = onMoveDown,
                enabled = canMoveDown
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = stringResource(id = R.string.move_safe_place_down)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(id = R.string.delete_safe_place),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

private fun List<SafePlace>.moveItem(fromIndex: Int, toIndex: Int): List<SafePlace> {
    if (fromIndex !in indices || toIndex !in indices) return this
    return toMutableList().also { places ->
        val movedPlace = places.removeAt(fromIndex)
        places.add(toIndex, movedPlace)
    }
}

