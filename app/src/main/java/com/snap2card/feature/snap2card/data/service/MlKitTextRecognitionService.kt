package com.snap2card.feature.snap2card.data.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.snap2card.feature.snap2card.domain.model.OcrResult
import com.snap2card.feature.snap2card.domain.service.OcrTextProcessor
import com.snap2card.feature.snap2card.domain.service.TextRecognitionService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MlKitTextRecognitionService @Inject constructor(
    @ApplicationContext private val context: Context,
) : TextRecognitionService {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override suspend fun recognizeText(uri: Uri): Result<OcrResult> = runCatching {
        val image = withContext(Dispatchers.IO) {
            InputImage.fromFilePath(context, uri)
        }
        recognizeImage(image)
    }

    override suspend fun recognizePdfText(uri: Uri, maxPages: Int): Result<OcrResult> = runCatching {
        val descriptor = withContext(Dispatchers.IO) {
            context.contentResolver.openFileDescriptor(uri, "r")
        } ?: throw IllegalArgumentException("Could not open PDF file")

        descriptor.use { fileDescriptor ->
            PdfRenderer(fileDescriptor).use { renderer ->
                if (renderer.pageCount > maxPages) {
                    throw IllegalArgumentException("PDFs can include up to $maxPages pages.")
                }

                val pageTexts = mutableListOf<String>()
                var blockCount = 0
                for (pageIndex in 0 until renderer.pageCount) {
                    renderer.openPage(pageIndex).use { page ->
                        val bitmap = renderPage(page)
                        try {
                            val result = recognizeImage(InputImage.fromBitmap(bitmap, 0))
                            pageTexts += result.text
                            blockCount += result.blockCount
                        } finally {
                            bitmap.recycle()
                        }
                    }
                }

                val cleanedText = OcrTextProcessor.clean(pageTexts.joinToString("\n\n"))
                OcrResult(
                    text = cleanedText,
                    characterCount = cleanedText.length,
                    blockCount = blockCount,
                )
            }
        }
    }

    private suspend fun recognizeImage(image: InputImage): OcrResult {
        val recognizedText = recognizer.process(image).await()
        val cleanedText = OcrTextProcessor.clean(recognizedText.text)
        return OcrResult(
            text = cleanedText,
            characterCount = cleanedText.length,
            blockCount = recognizedText.textBlocks.size,
        )
    }

    private fun renderPage(page: PdfRenderer.Page): Bitmap {
        val bitmap = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
        Canvas(bitmap).drawColor(Color.WHITE)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        return bitmap
    }
}
