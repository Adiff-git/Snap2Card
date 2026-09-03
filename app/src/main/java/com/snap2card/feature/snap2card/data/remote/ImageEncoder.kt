package com.snap2card.feature.snap2card.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.snap2card.feature.snap2card.data.remote.dto.ImageDto
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/**
 * Converts a captured/picked image (JPEG from camera, or whatever mime type
 * a document import gives us) into the PNG base64 data-URI format required by
 * object-types.md#image ("Only PNG images are supported").
 *
 * This belongs in the data layer — the domain layer and Compose screens
 * shouldn't know or care about this encoding requirement.
 */
class ImageEncoder @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    /**
     * Reads the image at [uri], re-encodes as PNG, and returns it as a
     * ready-to-send ImageDto. Returns null if the image can't be decoded.
     */
    fun encode(uri: Uri): ImageDto? {
        val bitmap = decodeBitmap(uri) ?: return null
        val base64 = bitmapToPngBase64(bitmap)
        bitmap.recycle()
        return ImageDto(
            image = "data:image/png;base64,$base64",
            mimeType = "image/png",
        )
    }

    private fun decodeBitmap(uri: Uri): Bitmap? {
        return context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream)
        }
    }

    private fun bitmapToPngBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}