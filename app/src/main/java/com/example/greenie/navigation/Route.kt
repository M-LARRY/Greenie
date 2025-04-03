package com.example.greenie.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    object Home : Route

    @Serializable
    object PlantList : Route

    @Serializable
    object SignIn : Route

    @Serializable
    object SignUp : Route
}