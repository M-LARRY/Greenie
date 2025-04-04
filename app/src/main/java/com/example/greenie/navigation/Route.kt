package com.example.greenie.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Home : Route

    @Serializable
    data class PlantList(val lat: Float, val lng: Float, val brightness: Float) : Route

    @Serializable
    data object SavedList : Route

    @Serializable
    data object SignIn : Route

    @Serializable
    data object SignUp : Route
}