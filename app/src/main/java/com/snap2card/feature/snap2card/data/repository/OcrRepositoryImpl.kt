package com.snap2card.feature.snap2card.data.repository

import android.net.Uri
import android.util.Log
import com.snap2card.core.util.FileUtil
import com.snap2card.feature.snap2card.data.remote.CardApiService
import com.snap2card.feature.snap2card.data.remote.dto.CardDocumentGeneratedCardDto
import com.snap2card.feature.snap2card.domain.model.OcrResult
import com.snap2card.feature.snap2card.domain.repository.OcrRepository
import com.snap2card.feature.snap2card.domain.service.OcrTextProcessor
import com.snap2card.feature.snap2card.domain.service.TextRecognitionService
import com.snap2card.feature.snap2card.domain.vocabulary.model.GeneratedVocabularyCard
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import javax.inject.Inject

class OcrRepositoryImpl @Inject constructor(
    private val textRecognitionService: TextRecognitionService,
    private val cardApiService: CardApiService,
) : OcrRepository {

    override suspend fun extractText(uri: Uri, mimeType: String?): Result<OcrResult> = try {
        val isPdf = FileUtil.isPdf(mimeType)
        val ocrResult = if (isPdf) {
            textRecognitionService.recognizePdfText(uri).getOrThrow()
        } else {
            textRecognitionService.recognizeText(uri).getOrThrow()
        }
        if (!OcrTextProcessor.hasReadableText(ocrResult.text)) {
            throw IllegalArgumentException(
                if (isPdf) "No readable text was detected in this PDF. Try another PDF file."
                else OcrTextProcessor.NO_READABLE_TEXT_MESSAGE
            )
        }
        Result.success(ocrResult)
    } catch (error: Throwable) {
        if (error is CancellationException) throw error
        Result.failure(error.toTextExtractionFailure(FileUtil.isPdf(mimeType)))
    }

    override suspend fun generateCardsFromText(
        text: String,
        sourceType: String,
    ): Result<List<GeneratedVocabularyCard>> = try {
        if (!OcrTextProcessor.hasReadableText(text)) {
            throw IllegalArgumentException(OcrTextProcessor.NO_READABLE_TEXT_MESSAGE)
        }
        Result.success(generateCardsFromDocument(text))
    } catch (error: Throwable) {
        if (error is CancellationException) throw error
        Result.failure(error.toGenerationFailure())
    }

    override suspend fun generateCards(uri: Uri, mimeType: String): Result<List<GeneratedVocabularyCard>> = try {
        val ocrResult = extractText(uri, mimeType).getOrThrow()
        val cards = generateCardsFromDocument(ocrResult.text)
        Result.success(cards)
    } catch (error: Throwable) {
        if (error is CancellationException) throw error
        Result.failure(error.toGenerationFailure())
    }

    private suspend fun generateCardsFromDocument(text: String): List<GeneratedVocabularyCard> =
        cardApiService.generateCardsFromDocument(
            text.toRequestBody("text/plain; charset=utf-8".toMediaType())
        ).data.cards.map { it.toDomain() }

    private fun CardDocumentGeneratedCardDto.toDomain() = GeneratedVocabularyCard(
        term = frontSide,
        definition = backSide,
        translation = "",
    )

    private fun Throwable.toGenerationFailure(): Throwable {
        if (this is IllegalArgumentException && !message.isNullOrBlank()) return this
        if (this !is HttpException) {
            return IllegalStateException("Could not generate cards. Check your connection and try again.", this)
        }

        val apiMessage = response()
            ?.errorBody()
            ?.string()
            ?.extractApiMessage()
        val message = buildString {
            append("Card generation service returned HTTP ")
            append(code())
            if (!apiMessage.isNullOrBlank()) {
                append(": ")
                append(apiMessage)
            }
        }
        return IllegalStateException(message, this)
    }

    private fun Throwable.toTextExtractionFailure(isPdf: Boolean): Throwable {
        if (this is IllegalArgumentException && !message.isNullOrBlank()) return this
        Log.e(TAG, "Text extraction failed. isPdf=$isPdf", this)
        val message = if (isPdf) {
            "Could not extract text from this PDF. Try another PDF file."
        } else {
            "Failed to scan text"
        }
        return IllegalStateException(message, this)
    }

    private fun String.extractApiMessage(): String? = runCatching {
        Json.parseToJsonElement(this)
            .jsonObject["message"]
            ?.jsonPrimitive
            ?.contentOrNull
    }.getOrNull()

    private companion object {
        const val TAG = "Snap2CardOcrRepo"
    }
}
