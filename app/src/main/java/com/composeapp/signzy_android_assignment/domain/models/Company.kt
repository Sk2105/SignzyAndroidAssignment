package com.composeapp.signzy_android_assignment.domain.models

import kotlinx.serialization.Serializable


@Serializable
data class Company(
    val address: Address,
    val department: String,
    val name: String,
    val title: String
)