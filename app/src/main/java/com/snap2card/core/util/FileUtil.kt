package com.snap2card.core.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

object FileUtil {

    fun createTempImageUri(context: Context): Uri {
        val imageDir = File(context.cacheDir, "images").apply { mkdirs() }
        val imageFile = File.createTempFile("scan_", ".jpg", imageDir)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile,
        )
    }

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

    fun getDisplayName(context: Context, uri: Uri): String =
        queryOpenableColumn(context, uri, OpenableColumns.DISPLAY_NAME)
            ?: uri.lastPathSegment
            ?: "Selected file"

    fun getFileSize(context: Context, uri: Uri): Long? =
        queryOpenableColumn(context, uri, OpenableColumns.SIZE)?.toLongOrNull()

    private fun queryOpenableColumn(context: Context, uri: Uri, column: String): String? {
        val cursor: Cursor = context.contentResolver.query(uri, arrayOf(column), null, null, null)
            ?: return null
        return cursor.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(column)
                if (index >= 0 && !it.isNull(index)) it.getString(index) else null
            } else {
                null
            }
        }
    }
}
