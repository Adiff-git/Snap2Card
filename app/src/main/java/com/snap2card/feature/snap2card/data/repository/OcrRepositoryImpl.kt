package com.snap2card.feature.snap2card.data.repository

import android.net.Uri
import com.snap2card.feature.snap2card.data.remote.CardApiService
import com.snap2card.feature.snap2card.data.remote.ImageEncoder
import com.snap2card.feature.snap2card.data.remote.dto.CardCreateRequest
import com.snap2card.feature.snap2card.domain.model.GeneratedCard
import com.snap2card.feature.snap2card.domain.repository.OcrRepository
import javax.inject.Inject

class OcrRepositoryImpl @Inject constructor(
    private val apiService: CardApiService,
    private val imageEncoder: ImageEncoder,
) : OcrRepository {

    override suspend fun submitImage(uri: Uri, mimeType: String, name: String): Result<String> {
        val imageDto = imageEncoder.encode(uri)
            ?: return Result.failure(IllegalArgumentException("Could not read image at $uri"))

        return runCatching {
            val response = apiService.createCard(
                CardCreateRequest(
                    name = name,
                    type = "image",
                    image = imageDto,
                )
            )
            response.data.id
        }
    }

    override suspend fun submitDocument(text: String, name: String): Result<String> {
        return runCatching {
            val response = apiService.createCard(
                CardCreateRequest(
                    name = name,
                    type = "document",
                    text = text,
                )
            )
            response.data.id
        }
    }

    override suspend fun getGeneratedCard(cardId: String): Result<GeneratedCard> {
        return runCatching {
            val response = apiService.getCards(ids = listOf(cardId))
            val card = response.data.firstOrNull()
                ?: throw NoSuchElementException("Card $cardId not found in response")
            GeneratedCard(
                id = card.id,
                front = card.frontSide,
                back = card.backSide,
            )
        }
    }
}