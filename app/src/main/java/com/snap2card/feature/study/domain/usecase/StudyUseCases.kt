package com.snap2card.feature.study.domain.usecase

import com.snap2card.feature.deck.domain.model.Card
import com.snap2card.feature.deck.domain.repository.DeckRepository
import com.snap2card.feature.study.domain.model.ReviewRecord
import com.snap2card.feature.study.domain.model.ReviewResult
import com.snap2card.feature.study.domain.repository.StudyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StudyDeckUseCase @Inject constructor(private val deckRepository: DeckRepository) {
    operator fun invoke(deckId: String): Flow<List<Card>> =
        deckRepository.getCardsForDeck(deckId)
}

class RecordReviewUseCase @Inject constructor(private val studyRepository: StudyRepository) {
    suspend operator fun invoke(cardId: String, deckId: String, result: ReviewResult) =
        studyRepository.recordReview(cardId, deckId, result)
}

class GetStudyHistoryUseCase @Inject constructor(private val studyRepository: StudyRepository) {
    operator fun invoke(): Flow<List<ReviewRecord>> = studyRepository.getAllReviews()
}
