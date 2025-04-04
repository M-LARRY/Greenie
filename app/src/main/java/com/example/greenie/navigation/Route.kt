package com.example.greenie.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    object Home : Route

    @Serializable
    data class PlantList(val lat: Double, val lng: Double, val brightness: Float) : Route

    @Serializable
    object SavedList : Route

    @Serializable
    object SignIn : Route

    @Serializable
    object SignUp : Route
}