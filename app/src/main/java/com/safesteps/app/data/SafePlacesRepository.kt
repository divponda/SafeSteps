package com.safesteps.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.safesteps.app.utils.SafePlacesStorageConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
private data class SafePlaceSerializable(
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val address: String = ""
)

class SafePlacesRepository(private val context: Context) {

    private val safePlacesKey = stringPreferencesKey(SafePlacesStorageConstants.SafePlacesKey)

    val safePlaces: Flow<List<SafePlace>?> = context.dataStore.data
        .map { preferences ->
            val json = preferences[safePlacesKey] ?: return@map null
            try {
                Json.decodeFromString<List<SafePlaceSerializable>>(json).map { it.toSafePlace() }
            } catch (e: Exception) {
                null
            }
        }

    suspend fun saveSafePlaces(places: List<SafePlace>) {
        context.dataStore.edit { preferences ->
            preferences[safePlacesKey] = Json.encodeToString(places.map { it.toSerializable() })
        }
    }

    private fun SafePlace.toSerializable() = SafePlaceSerializable(title, description, latitude, longitude, address)

    private fun SafePlaceSerializable.toSafePlace() = SafePlace(title, description, latitude, longitude, address)
}
