package com.composeapp.signzy_android_assignment.data.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.composeapp.signzy_android_assignment.domain.models.VerificationStatus
import java.io.ByteArrayOutputStream

class RoomConverters {


    @TypeConverter
    fun fromBitmap(bitmap: Bitmap?): ByteArray? {
        if (bitmap == null) return null
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray?): Bitmap? {
        if (byteArray == null) return null
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }


    @TypeConverter
    fun fromStatus(status: VerificationStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(value: String): VerificationStatus {
        return VerificationStatus.valueOf(value)
    }
}