package com.safesteps.app.screens

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.safesteps.app.R
import com.safesteps.app.data.model.SafePlace
import com.safesteps.app.data.model.SafePlaceCategory
import com.safesteps.app.data.repository.DemoSafePlacesRepository
import com.safesteps.app.ui.components.SafeStepsCard
import com.safesteps.app.ui.components.StatusPill
import com.safesteps.app.utils.LocationConstants

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val safePlacesRepository = remember {
        DemoSafePlacesRepository(context.applicationContext)
    }
    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LocationConstants.DefaultLocation,
            LocationConstants.DefaultMapZoom
        )
    }

    val isLocationGranted = locationPermissionState.status.isGranted
    var isMapLoaded by remember { mutableStateOf(false) }
    var isLoadingSafePlaces by remember { mutableStateOf(false) }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var mapCenter by remember { mutableStateOf(LocationConstants.DefaultLocation) }
    var safePlaces by remember { mutableStateOf(emptyList<SafePlace>()) }
    var statusMessageRes by remember { mutableStateOf<Int?>(null) }
    var refreshCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(isLocationGranted, refreshCount) {
        refreshNearbySafePlaces(
            context = context,
            repository = safePlacesRepository,
            isLocationGranted = isLocationGranted,
            onLoadingChanged = { isLoadingSafePlaces = it },
            onCurrentLocationChanged = { currentLocation = it },
            onMapCenterChanged = { mapCenter = it },
            onSafePlacesChanged = { safePlaces = it },
            onStatusMessageChanged = { statusMessageRes = it }
        )
    }

    LaunchedEffect(isMapLoaded, mapCenter) {
        if (isMapLoaded) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    mapCenter,
                    LocationConstants.DefaultMapZoom
                )
            )
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(dimensionResource(id = R.dimen.spacing_large))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.map_height))
                .clip(MaterialTheme.shapes.large)
        ) {
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
                    val categoryName = safePlaceCategoryName(safePlace.category)
                    val distanceLabel = distanceLabel(safePlace.distanceMeters)
                    Marker(
                        state = MarkerState(position = LatLng(safePlace.latitude, safePlace.longitude)),
                        title = safePlace.name,
                        snippet = stringResource(
                            id = R.string.safe_place_marker_snippet,
                            categoryName,
                            distanceLabel
                        ),
                        icon = BitmapDescriptorFactory.defaultMarker(safePlaceMarkerHue(safePlace.category))
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
                    statusMessageRes?.let { messageRes ->
                        Text(
                            text = stringResource(id = messageRes),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = dimensionResource(id = R.dimen.spacing_small))
                        )
                    }
                    Row(
                        modifier = Modifier.padding(top = dimensionResource(id = R.dimen.spacing_medium)),
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_small))
                    ) {
                        StatusPill(
                            text = if (currentLocation != null) {
                                stringResource(id = R.string.map_location_detected)
                            } else {
                                stringResource(id = R.string.map_location_fallback)
                            },
                            color = if (currentLocation != null) {
                                MaterialTheme.colorScheme.secondary
                            } else {
                                MaterialTheme.colorScheme.error
                            }
                        )
                        StatusPill(
                            text = stringResource(id = R.string.nearby_safe_places_count, safePlaces.size),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (!isLocationGranted) {
                        Button(
                            onClick = { locationPermissionState.launchPermissionRequest() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = dimensionResource(id = R.dimen.spacing_small)),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(text = stringResource(id = R.string.location_permission_button))
                        }
                    }
                }
            }

            if (!isMapLoaded || isLoadingSafePlaces) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    strokeWidth = dimensionResource(id = R.dimen.spacing_xsmall)
                )
            }

            FilledTonalButton(
                onClick = { refreshCount += 1 },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(dimensionResource(id = R.dimen.spacing_large)),
                shape = MaterialTheme.shapes.large
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    modifier = Modifier.size(dimensionResource(id = R.dimen.primary_icon_size))
                )
                Text(
                    text = stringResource(id = R.string.refresh_safe_places),
                    modifier = Modifier.padding(start = dimensionResource(id = R.dimen.spacing_small))
                )
            }
        }

        SafePlacesList(
            safePlaces = safePlaces,
            isLoading = isLoadingSafePlaces,
            onRefresh = { refreshCount += 1 },
            onPlaceSelected = { safePlace ->
                mapCenter = LatLng(safePlace.latitude, safePlace.longitude)
            },
            onNavigate = { safePlace ->
                openGoogleMapsNavigation(context, safePlace)
            }
        )
    }
}

@Composable
private fun SafePlacesList(
    safePlaces: List<SafePlace>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onPlaceSelected: (SafePlace) -> Unit,
    onNavigate: (SafePlace) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dimensionResource(id = R.dimen.spacing_large))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.nearby_safe_places),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            FilledTonalButton(
                onClick = onRefresh,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = stringResource(id = R.string.refresh_safe_places))
            }
        }
        Text(
            text = if (isLoading) {
                stringResource(id = R.string.map_status_scanning)
            } else {
                stringResource(id = R.string.map_status_ready)
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = dimensionResource(id = R.dimen.spacing_xsmall))
        )

        when {
            isLoading -> {
                Text(
                    text = stringResource(id = R.string.loading_safe_places),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = dimensionResource(id = R.dimen.spacing_small))
                )
            }

            safePlaces.isEmpty() -> {
                Text(
                    text = stringResource(id = R.string.no_safe_places_found),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = dimensionResource(id = R.dimen.spacing_small))
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(top = dimensionResource(id = R.dimen.spacing_small))
                        .heightIn(max = dimensionResource(id = R.dimen.safe_place_list_max_height)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_small))
                ) {
                    items(safePlaces) { safePlace ->
                        SafePlaceListItem(
                            safePlace = safePlace,
                            onPlaceSelected = { onPlaceSelected(safePlace) },
                            onNavigate = { onNavigate(safePlace) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SafePlaceListItem(
    safePlace: SafePlace,
    onPlaceSelected: () -> Unit,
    onNavigate: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.spacing_xsmall)),
        onClick = onPlaceSelected
    ) {
        Row(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.spacing_large))
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.safe_place_marker_size))
                    .background(categoryAccentColor(safePlace.category).copy(alpha = 0.16f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (safePlace.category == SafePlaceCategory.Hospital ||
                        safePlace.category == SafePlaceCategory.FireStation
                    ) {
                        Icons.Default.Warning
                    } else {
                        Icons.Default.Place
                    },
                    contentDescription = null,
                    tint = categoryAccentColor(safePlace.category),
                    modifier = Modifier.size(dimensionResource(id = R.dimen.primary_icon_size))
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = dimensionResource(id = R.dimen.spacing_medium))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = safePlace.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = safePlaceCategoryName(safePlace.category),
                            style = MaterialTheme.typography.bodyMedium,
                            color = categoryAccentColor(safePlace.category),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = distanceLabel(safePlace.distanceMeters),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.shapes.small
                            )
                            .padding(
                                horizontal = dimensionResource(id = R.dimen.spacing_small),
                                vertical = dimensionResource(id = R.dimen.spacing_xsmall)
                            )
                    )
                }
                safePlace.address?.let { address ->
                    Text(
                        text = address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = dimensionResource(id = R.dimen.spacing_small))
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    safePlace.rating?.let { rating ->
                        Text(
                            text = stringResource(id = R.string.place_rating, rating),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = if (safePlace.isOpenNow == true) {
                            stringResource(id = R.string.place_open_now)
                        } else {
                            stringResource(id = R.string.place_open_unknown)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(
                            start = if (safePlace.rating != null) {
                                dimensionResource(id = R.dimen.spacing_medium)
                            } else {
                                dimensionResource(id = R.dimen.spacing_xsmall)
                            }
                        )
                    )
                }
                TextButton(
                    onClick = onNavigate,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = stringResource(id = R.string.navigate_to_place))
                }
            }
        }
    }
}

private fun refreshNearbySafePlaces(
    context: Context,
    repository: DemoSafePlacesRepository,
    isLocationGranted: Boolean,
    onLoadingChanged: (Boolean) -> Unit,
    onCurrentLocationChanged: (LatLng?) -> Unit,
    onMapCenterChanged: (LatLng) -> Unit,
    onSafePlacesChanged: (List<SafePlace>) -> Unit,
    onStatusMessageChanged: (Int?) -> Unit
) {
    onLoadingChanged(true)

    fun loadForLocation(location: LatLng, statusMessageRes: Int?) {
        val safePlaces = repository.getNearbySafePlaces(location.latitude, location.longitude)
        onMapCenterChanged(location)
        onSafePlacesChanged(safePlaces)
        onStatusMessageChanged(
            statusMessageRes ?: if (safePlaces.isEmpty()) R.string.no_safe_places_found else null
        )
        onLoadingChanged(false)
    }

    if (
        !isLocationGranted ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
        PackageManager.PERMISSION_GRANTED
    ) {
        onCurrentLocationChanged(null)
        loadForLocation(LocationConstants.DefaultLocation, R.string.location_permission_message)
        return
    }

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val cancellationTokenSource = CancellationTokenSource()
    fusedLocationClient.getCurrentLocation(
        Priority.PRIORITY_HIGH_ACCURACY,
        cancellationTokenSource.token
    )
        .addOnSuccessListener { location ->
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                onCurrentLocationChanged(latLng)
                loadForLocation(latLng, null)
            } else {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { lastLocation ->
                        if (lastLocation != null) {
                            val latLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                            onCurrentLocationChanged(latLng)
                            loadForLocation(latLng, null)
                        } else {
                            onCurrentLocationChanged(null)
                            loadForLocation(
                                LocationConstants.DefaultLocation,
                                R.string.location_unavailable_message
                            )
                        }
                    }
                    .addOnFailureListener {
                        onCurrentLocationChanged(null)
                        loadForLocation(LocationConstants.DefaultLocation, R.string.location_unavailable_message)
                    }
            }
        }
        .addOnFailureListener {
            onCurrentLocationChanged(null)
            loadForLocation(LocationConstants.DefaultLocation, R.string.safe_places_load_error)
        }
}

@Composable
private fun safePlaceCategoryName(category: SafePlaceCategory): String {
    return when (category) {
        SafePlaceCategory.PoliceStation -> stringResource(id = R.string.category_police_station)
        SafePlaceCategory.Hospital -> stringResource(id = R.string.category_hospital)
        SafePlaceCategory.Pharmacy -> stringResource(id = R.string.category_pharmacy)
        SafePlaceCategory.FireStation -> stringResource(id = R.string.category_fire_station)
        SafePlaceCategory.Library -> stringResource(id = R.string.category_library)
        SafePlaceCategory.TransitStation -> stringResource(id = R.string.category_transit_station)
        SafePlaceCategory.ConvenienceStore -> stringResource(id = R.string.category_convenience_store)
        SafePlaceCategory.ShoppingCenter -> stringResource(id = R.string.category_shopping_center)
        SafePlaceCategory.Hotel -> stringResource(id = R.string.category_hotel)
        SafePlaceCategory.Bank -> stringResource(id = R.string.category_bank)
        SafePlaceCategory.GovernmentBuilding -> stringResource(id = R.string.category_government_building)
        SafePlaceCategory.CommunityCenter -> stringResource(id = R.string.category_community_center)
        SafePlaceCategory.Embassy -> stringResource(id = R.string.category_embassy)
        SafePlaceCategory.Other -> stringResource(id = R.string.category_other)
    }
}

@Composable
private fun distanceLabel(distanceMeters: Double?): String {
    if (distanceMeters == null) return stringResource(id = R.string.category_other)
    return if (distanceMeters < MetersPerKilometer) {
        stringResource(id = R.string.distance_meters, distanceMeters.toInt())
    } else {
        stringResource(id = R.string.distance_kilometers, distanceMeters / MetersPerKilometer)
    }
}

@Composable
private fun categoryAccentColor(category: SafePlaceCategory): Color {
    return when (category) {
        SafePlaceCategory.PoliceStation -> MaterialTheme.colorScheme.primary
        SafePlaceCategory.Hospital -> MaterialTheme.colorScheme.error
        SafePlaceCategory.Pharmacy -> MaterialTheme.colorScheme.secondary
        SafePlaceCategory.FireStation -> MaterialTheme.colorScheme.error
        SafePlaceCategory.Library -> MaterialTheme.colorScheme.tertiary
        SafePlaceCategory.TransitStation -> MaterialTheme.colorScheme.primary
        SafePlaceCategory.ShoppingCenter -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.secondary
    }
}

private fun safePlaceMarkerHue(category: SafePlaceCategory): Float {
    return when (category) {
        SafePlaceCategory.PoliceStation -> BitmapDescriptorFactory.HUE_BLUE
        SafePlaceCategory.Hospital -> BitmapDescriptorFactory.HUE_RED
        SafePlaceCategory.Pharmacy -> BitmapDescriptorFactory.HUE_GREEN
        SafePlaceCategory.FireStation -> BitmapDescriptorFactory.HUE_ORANGE
        SafePlaceCategory.Library -> BitmapDescriptorFactory.HUE_AZURE
        SafePlaceCategory.TransitStation -> BitmapDescriptorFactory.HUE_VIOLET
        SafePlaceCategory.ShoppingCenter -> BitmapDescriptorFactory.HUE_YELLOW
        else -> BitmapDescriptorFactory.HUE_ROSE
    }
}

private fun openGoogleMapsNavigation(context: Context, safePlace: SafePlace) {
    val navigationIntent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("google.navigation:q=${safePlace.latitude},${safePlace.longitude}")
    }

    try {
        context.startActivity(navigationIntent)
    } catch (exception: ActivityNotFoundException) {
        Toast.makeText(
            context,
            context.getString(R.string.alert_app_missing),
            Toast.LENGTH_LONG
        ).show()
    }
}

private const val MetersPerKilometer = 1_000.0
