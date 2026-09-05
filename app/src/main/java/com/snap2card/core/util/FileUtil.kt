package com.snap2card.core.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
        val mimeType = getMimeType(context, uri)
        val tempFile = copyUriToTempFile(context, uri, extensionForMimeType(mimeType))
        val requestBody = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, tempFile.name, requestBody)
    }

    fun uriToRequestBody(
        context: Context,
        uri: Uri,
        mimeType: String = context.contentResolver.getType(uri) ?: "application/octet-stream",
    ): RequestBody {
        val tempFile = copyUriToTempFile(context, uri, extensionForMimeType(mimeType))
        return tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
    }

    fun copyUriToCacheUri(context: Context, uri: Uri, extension: String = "bin"): Uri {
        val tempFile = copyUriToTempFile(context, uri, extension)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile,
        )
    }

    fun copyUriToTempFile(context: Context, uri: Uri, extension: String = "bin"): File {
        val tempFile = File(context.cacheDir, "upload_${System.currentTimeMillis()}.$extension")
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Could not open selected file")
        inputStream.use { input ->
            FileOutputStream(tempFile).use { output -> input.copyTo(output) }
        }
        return tempFile
    }

    fun getMimeType(context: Context, uri: Uri): String {
        val resolverMimeType = context.contentResolver.getType(uri)
        val displayName = getDisplayName(context, uri)
        return if (isPdf(resolverMimeType, displayName)) {
            "application/pdf"
        } else {
            resolverMimeType ?: "application/octet-stream"
        }
    }

    fun isPdf(mimeType: String?, fileName: String? = null): Boolean =
        mimeType.equals("application/pdf", ignoreCase = true) ||
            fileName?.endsWith(".pdf", ignoreCase = true) == true

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

    fun extensionForMimeType(mimeType: String): String =
        MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "bin"
}
