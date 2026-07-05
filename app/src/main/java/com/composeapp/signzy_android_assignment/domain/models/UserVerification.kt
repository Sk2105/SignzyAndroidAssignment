package com.composeapp.signzy_android_assignment.domain.models

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.composeapp.signzy_android_assignment.utils.BitmapAsBase64Serializer
import kotlinx.serialization.Serializable


@Serializable
@Entity(tableName = "user_verifications")
data class UserVerification(
    @PrimaryKey val userId: Int,
    val verificationStatus: VerificationStatus,
    @Serializable(with = BitmapAsBase64Serializer::class)
    val capturedPhoto: Bitmap? = null
)
