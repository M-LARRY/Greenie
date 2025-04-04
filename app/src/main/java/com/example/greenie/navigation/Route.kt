package com.example.greenie.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Home : Route

    @Serializable
    data object PlantList : Route

    @Serializable
    data object SavedList : Route

    @Serializable
    data object SignIn : Route

    @Serializable
    data object SignUp : Route
}