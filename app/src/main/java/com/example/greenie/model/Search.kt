package com.example.greenie.model

import kotlinx.serialization.Serializable

data class Search (
    val lng : Double,
    val lat : Double,
    val brightness : Float,
)