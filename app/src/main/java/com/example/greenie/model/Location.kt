package com.example.greenie.model

data class Location(val latitude: Double = 0.0, val longitude: Double = 0.0) {
    constructor(location: android.location.Location) : this(location.latitude, location.longitude)

    fun found() = latitude != 0.0 && longitude != 0.0
}