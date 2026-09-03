package com.snap2card.feature.snap2card.data.repository

import android.content.Context
import android.net.Uri
import com.snap2card.core.util.FileUtil
import com.snap2card.feature.snap2card.domain.repository.OcrRepository
import com.snap2card.feature.snap2card.domain.service.OcrTextProcessor
import com.snap2card.feature.snap2card.domain.service.TextRecognitionService
import com.snap2card.feature.snap2card.data.vocabulary.mapper.toDomain
import com.snap2card.feature.snap2card.data.vocabulary.remote.VocabularyApiService
import com.snap2card.feature.snap2card.data.vocabulary.remote.dto.VocabularyFromTextRequest
import com.snap2card.feature.snap2card.domain.vocabulary.model.GeneratedVocabularyCard
import com.snap2card.feature.snap2card.domain.vocabulary.model.VocabularyGenerationDefaults
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import javax.inject.Inject

class OcrRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val textRecognitionService: TextRecognitionService,
    private val vocabularyApiService: VocabularyApiService,
) : OcrRepository {

    override suspend fun generateCards(uri: Uri, mimeType: String): Result<List<GeneratedVocabularyCard>> = try {
            val response = if (mimeType == "application/pdf") {
                vocabularyApiService.generateVocabularyFromPdf(
                    file = FileUtil.uriToMultipart(context, uri),
                    level = VocabularyGenerationDefaults.LEVEL.toRequestBody(),
                    count = VocabularyGenerationDefaults.COUNT.toString().toRequestBody(),
                    includePhrases = VocabularyGenerationDefaults.INCLUDE_PHRASES.toString().toRequestBody(),
                )
            } else {
                val ocrResult = textRecognitionService.recognizeText(uri).getOrThrow()
                if (!OcrTextProcessor.hasReadableText(ocrResult.text)) {
                    throw IllegalArgumentException(OcrTextProcessor.NO_READABLE_TEXT_MESSAGE)
                }
                vocabularyApiService.generateVocabularyFromText(
                    VocabularyFromTextRequest(
                        text = ocrResult.text,
                        level = VocabularyGenerationDefaults.LEVEL,
                        count = VocabularyGenerationDefaults.COUNT,
                        includePhrases = VocabularyGenerationDefaults.INCLUDE_PHRASES,
                        sourceType = "scan",
                    )
                )
            }
            Result.success(response.data.cards.toDomain())
        } catch (error: Throwable) {
            if (error is CancellationException) throw error
            Result.failure(error.toGenerationFailure())
        }

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
            append("Vocabulary service returned HTTP ")
            append(code())
            if (!apiMessage.isNullOrBlank()) {
                append(": ")
                append(apiMessage)
            }
        }
        return IllegalStateException(message, this)
    }

    private fun String.extractApiMessage(): String? = runCatching {
        Json.parseToJsonElement(this)
            .jsonObject["message"]
            ?.jsonPrimitive
            ?.contentOrNull
    }.getOrNull()
}
