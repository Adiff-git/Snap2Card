package com.snap2card.feature.snap2card.data.service

import android.content.Context
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
        val recognizedText = recognizer.process(image).await()
        val cleanedText = OcrTextProcessor.clean(recognizedText.text)

        OcrResult(
            text = cleanedText,
            characterCount = cleanedText.length,
            blockCount = recognizedText.textBlocks.size,
        )
    }
}
