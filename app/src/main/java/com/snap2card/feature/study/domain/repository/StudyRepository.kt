package com.snap2card.feature.study.domain.repository

import com.snap2card.feature.study.domain.model.ReviewRecord
import com.snap2card.feature.study.domain.model.ReviewResult
import kotlinx.coroutines.flow.Flow

interface StudyRepository {
    suspend fun recordReview(cardId: String, deckId: String, result: ReviewResult)
    fun getReviewsForDeck(deckId: String): Flow<List<ReviewRecord>>
    fun getAllReviews(): Flow<List<ReviewRecord>>
}
