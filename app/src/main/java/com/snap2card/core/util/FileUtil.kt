package com.snap2card.core.util

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

object FileUtil {

    /** Copies a content URI to a temp file and returns a Retrofit MultipartBody.Part. */
    fun uriToMultipart(context: Context, uri: Uri, partName: String = "file"): MultipartBody.Part {
        val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
        val extension = MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(mimeType) ?: "bin"
        val tempFile = File(context.cacheDir, "upload_${System.currentTimeMillis()}.$extension")
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(tempFile).use { output -> input.copyTo(output) }
        }
        val requestBody = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, tempFile.name, requestBody)
    }

    fun getMimeType(context: Context, uri: Uri): String =
        context.contentResolver.getType(uri) ?: "application/octet-stream"
}
