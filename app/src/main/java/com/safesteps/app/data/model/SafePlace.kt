package com.safesteps.app.data.model

enum class SafePlaceCategory {
    PoliceStation,
    Hospital,
    Pharmacy,
    FireStation,
    Library,
    TransitStation,
    ConvenienceStore,
    ShoppingCenter,
    Hotel,
    Bank,
    GovernmentBuilding,
    CommunityCenter,
    Embassy,
    Other
}

data class SafePlace(
    val id: String,
    val name: String,
    val category: SafePlaceCategory,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val distanceMeters: Double?,
    val rating: Double?,
    val isOpenNow: Boolean?
)
