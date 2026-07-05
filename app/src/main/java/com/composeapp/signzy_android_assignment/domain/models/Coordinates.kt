package com.composeapp.signzy_android_assignment.domain.models

import kotlinx.serialization.Serializable


@Serializable
data class Coordinates(
    val lat: Double,
    val lng: Double
)