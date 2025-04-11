package com.example.greenie

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

fun String.convertToBitMap(): Bitmap? {
    return try {
        val decodedBytes: ByteArray = Base64.decode(
            this.substring(this.indexOf(",") + 1),
            Base64.DEFAULT
        )

        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        null
    }
}

fun Bitmap.convertToBase64(): String {
    val outputStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

    return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
}