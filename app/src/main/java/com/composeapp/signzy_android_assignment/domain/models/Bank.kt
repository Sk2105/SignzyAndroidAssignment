package com.composeapp.signzy_android_assignment.domain.models

import kotlinx.serialization.Serializable


@Serializable
data class Bank(
    val cardExpire: String,
    val cardNumber: String,
    val cardType: String,
    val currency: String,
    val iban: String
)