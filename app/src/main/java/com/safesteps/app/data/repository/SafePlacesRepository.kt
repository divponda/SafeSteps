package com.safesteps.app.data.repository

import android.content.Context
import android.location.Location
import com.safesteps.app.R
import com.safesteps.app.data.model.SafePlace
import com.safesteps.app.data.model.SafePlaceCategory
import com.safesteps.app.utils.LocationConstants
import kotlin.math.cos

interface SafePlacesRepository {
    fun getNearbySafePlaces(latitude: Double, longitude: Double): List<SafePlace>
}

class DemoSafePlacesRepository(private val context: Context) : SafePlacesRepository {

    override fun getNearbySafePlaces(latitude: Double, longitude: Double): List<SafePlace> {
        val origin = Location("").apply {
            this.latitude = latitude
            this.longitude = longitude
        }

        return safePlaceTemplates.mapIndexed { index, template ->
            val placeLocation = offsetLocation(
                latitude = latitude,
                longitude = longitude,
                northMeters = template.northMeters,
                eastMeters = template.eastMeters
            )
            val distance = origin.distanceTo(
                Location("").apply {
                    this.latitude = placeLocation.first
                    this.longitude = placeLocation.second
                }
            ).toDouble()

            SafePlace(
                id = "demo_safe_place_$index",
                name = context.getString(template.nameRes),
                category = template.category,
                latitude = placeLocation.first,
                longitude = placeLocation.second,
                address = context.getString(template.addressRes),
                distanceMeters = distance,
                rating = template.rating,
                isOpenNow = template.isOpenNow
            )
        }
            .sortedBy { it.distanceMeters ?: Double.MAX_VALUE }
            .take(LocationConstants.MAX_SAFE_PLACES_TO_DISPLAY)
    }

    private fun offsetLocation(
        latitude: Double,
        longitude: Double,
        northMeters: Double,
        eastMeters: Double
    ): Pair<Double, Double> {
        val latitudeOffset = northMeters / METERS_PER_DEGREE_LATITUDE
        val longitudeOffset = eastMeters / (METERS_PER_DEGREE_LATITUDE * cos(Math.toRadians(latitude)))
        return latitude + latitudeOffset to longitude + longitudeOffset
    }

    private data class SafePlaceTemplate(
        val nameRes: Int,
        val addressRes: Int,
        val category: SafePlaceCategory,
        val northMeters: Double,
        val eastMeters: Double,
        val rating: Double?,
        val isOpenNow: Boolean?
    )

    private companion object {
        const val METERS_PER_DEGREE_LATITUDE = 111_320.0

        val safePlaceTemplates = listOf(
            SafePlaceTemplate(
                R.string.police_station_name,
                R.string.police_station_desc,
                SafePlaceCategory.PoliceStation,
                280.0,
                -180.0,
                4.5,
                true
            ),
            SafePlaceTemplate(
                R.string.hospital_name,
                R.string.hospital_desc,
                SafePlaceCategory.Hospital,
                -420.0,
                260.0,
                4.2,
                true
            ),
            SafePlaceTemplate(
                R.string.pharmacy_name,
                R.string.pharmacy_desc,
                SafePlaceCategory.Pharmacy,
                160.0,
                520.0,
                4.0,
                true
            ),
            SafePlaceTemplate(
                R.string.fire_station_name,
                R.string.fire_station_desc,
                SafePlaceCategory.FireStation,
                -650.0,
                -350.0,
                null,
                true
            ),
            SafePlaceTemplate(
                R.string.public_library_name,
                R.string.public_library_desc,
                SafePlaceCategory.Library,
                780.0,
                120.0,
                4.4,
                true
            ),
            SafePlaceTemplate(
                R.string.metro_station_name,
                R.string.metro_station_desc,
                SafePlaceCategory.TransitStation,
                -180.0,
                -760.0,
                4.1,
                true
            ),
            SafePlaceTemplate(
                R.string.hotel_name,
                R.string.hotel_desc,
                SafePlaceCategory.Hotel,
                920.0,
                -530.0,
                4.3,
                true
            ),
            SafePlaceTemplate(
                R.string.shopping_center_name,
                R.string.shopping_center_desc,
                SafePlaceCategory.ShoppingCenter,
                -980.0,
                620.0,
                4.2,
                true
            )
        )
    }
}
