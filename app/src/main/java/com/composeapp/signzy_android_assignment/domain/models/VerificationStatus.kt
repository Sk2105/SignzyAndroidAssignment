package com.composeapp.signzy_android_assignment.domain.models

import androidx.room.Entity
import kotlinx.serialization.Serializable


@Serializable
enum class VerificationStatus{
    PENDING,
    APPROVED,
    REJECTED
}