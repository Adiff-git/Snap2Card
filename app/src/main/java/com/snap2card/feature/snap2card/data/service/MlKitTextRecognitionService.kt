package com.snap2card.feature.snap2card.data.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.snap2card.core.util.FileUtil
import com.snap2card.feature.snap2card.domain.model.OcrResult
import com.snap2card.feature.snap2card.domain.service.OcrTextProcessor
import com.snap2card.feature.snap2card.domain.service.TextRecognitionService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

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
        withContext(Dispatchers.IO) {
            var stage = "copying the selected PDF"
            val pdfFile = try {
                Log.d(TAG, "Starting PDF OCR. uri=$uri maxPages=$maxPages")
                FileUtil.copyUriToTempFile(context, uri, "pdf")
            } catch (error: Throwable) {
                Log.e(TAG, "PDF OCR failed while $stage", error)
                throw error
            }

            try {
                Log.d(TAG, "Copied PDF for OCR. path=${pdfFile.absolutePath} size=${pdfFile.length()}")
                stage = "opening the copied PDF"
                val descriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
                    ?: throw IllegalArgumentException("Could not open PDF file")

                descriptor.use { fileDescriptor ->
                    stage = "creating PdfRenderer"
                    PdfRenderer(fileDescriptor).use { renderer ->
                        Log.d(TAG, "Opened PDF renderer. pageCount=${renderer.pageCount}")
                        if (renderer.pageCount == 0) {
                            throw IllegalArgumentException("This PDF does not contain any pages.")
                        }

                        if (renderer.pageCount > maxPages) {
                            throw IllegalArgumentException("PDFs can include up to $maxPages pages.")
                        }

                        val pageTexts = mutableListOf<String>()
                        var blockCount = 0
                        for (pageIndex in 0 until renderer.pageCount) {
                            stage = "rendering PDF page ${pageIndex + 1}"
                            renderer.openPage(pageIndex).use { page ->
                                val bitmap = renderPage(page, pageIndex)
                                try {
                                    stage = "recognizing text on PDF page ${pageIndex + 1}"
                                    val result = recognizeImage(InputImage.fromBitmap(bitmap, 0))
                                    Log.d(
                                        TAG,
                                        "Recognized PDF page ${pageIndex + 1}. chars=${result.characterCount} blocks=${result.blockCount}"
                                    )
                                    pageTexts += result.text
                                    blockCount += result.blockCount
                                } finally {
                                    bitmap.recycle()
                                }
                            }
                        }

                        stage = "cleaning recognized PDF text"
                        val cleanedText = OcrTextProcessor.clean(pageTexts.joinToString("\n\n"))
                        Log.d(TAG, "Finished PDF OCR. chars=${cleanedText.length} blocks=$blockCount")
                        OcrResult(
                            text = cleanedText,
                            characterCount = cleanedText.length,
                            blockCount = blockCount,
                        )
                    }
                }
            } catch (error: Throwable) {
                Log.e(TAG, "PDF OCR failed while $stage", error)
                throw error
            } finally {
                val deleted = pdfFile.delete()
                Log.d(TAG, "Deleted copied PDF after OCR. deleted=$deleted")
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

    private fun renderPage(page: PdfRenderer.Page, pageIndex: Int): Bitmap {
        val scale = minOf(2f, 2400f / maxOf(page.width, page.height)).coerceAtLeast(0.25f)
        val width = maxOf(1, (page.width * scale).roundToInt())
        val height = maxOf(1, (page.height * scale).roundToInt())
        Log.d(
            TAG,
            "Rendering PDF page ${pageIndex + 1}. page=${page.width}x${page.height} bitmap=${width}x$height scale=$scale"
        )
        val bitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888,
        )
        Canvas(bitmap).drawColor(Color.WHITE)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        return bitmap
    }

    private companion object {
        const val TAG = "Snap2CardPdfOcr"
    }
}
