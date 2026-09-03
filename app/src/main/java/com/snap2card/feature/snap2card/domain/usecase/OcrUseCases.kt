package com.snap2card.feature.snap2card.domain.usecase

import android.net.Uri
import com.snap2card.feature.card_generation.domain.model.GeneratedVocabularyCard
import com.snap2card.feature.deck.domain.model.Card
import com.snap2card.feature.deck.domain.repository.DeckRepository
import com.snap2card.feature.snap2card.domain.model.GeneratedCard
import com.snap2card.feature.snap2card.domain.repository.OcrRepository
import javax.inject.Inject

class UploadImageForOcrUseCase @Inject constructor(
    private val ocrRepository: OcrRepository
) {
    suspend operator fun invoke(uri: Uri, mimeType: String): Result<List<GeneratedVocabularyCard>> =
        ocrRepository.generateCards(uri, mimeType)
}

class SaveGeneratedCardsUseCase @Inject constructor(
    private val deckRepository: DeckRepository
) {
    suspend operator fun invoke(
        deckId: String,
        cards: List<GeneratedCard>
    ): List<Card> = cards.map { generated ->
        deckRepository.addCard(deckId, generated.front, generated.back)
    }
}
