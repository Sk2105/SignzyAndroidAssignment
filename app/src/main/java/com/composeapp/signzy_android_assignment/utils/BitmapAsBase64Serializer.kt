package com.composeapp.signzy_android_assignment.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.ByteArrayOutputStream

object BitmapAsBase64Serializer : KSerializer<Bitmap> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("BitmapSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Bitmap) {
        val outputStream = ByteArrayOutputStream()
        value.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
        encoder.encodeString(base64String)
    }

    override fun deserialize(decoder: Decoder): Bitmap {
        val base64String = decoder.decodeString()
        val byteArray = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}