package com.snap2card.feature.study.domain.usecase

import com.snap2card.feature.study.domain.model.StudyCard
import com.snap2card.feature.study.domain.repository.StudyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetDueCardsUseCase @Inject constructor(
    private val studyDeckUseCase: StudyDeckUseCase,
    private val studyRepository: StudyRepository,
    private val calculateNextReviewUseCase: CalculateNextReviewUseCase,
) {
    operator fun invoke(deckId: String): Flow<List<StudyCard>> =
        combine(
            studyDeckUseCase(deckId),
            studyRepository.getReviewsForDeck(deckId),
        ) { cards, allReviews ->
            val now = System.currentTimeMillis()
            cards.filter { card ->
                val cardReviews = allReviews.filter { it.cardId == card.id }
                val state = calculateNextReviewUseCase.deriveState(cardReviews)
                calculateNextReviewUseCase.isDue(state, now)
            }.map { card ->
                StudyCard(cardId = card.id, deckId = deckId, front = card.front, back = card.back)
            }
        }
}