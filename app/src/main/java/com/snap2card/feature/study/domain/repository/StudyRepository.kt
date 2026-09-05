package com.snap2card.feature.study.domain.repository

import com.snap2card.feature.study.domain.model.ExamReviewDetail
import com.snap2card.feature.study.domain.model.ExamSession
import com.snap2card.feature.study.domain.model.ReviewQuiz
import com.snap2card.feature.study.domain.model.ReviewRecord
import com.snap2card.feature.study.domain.model.ReviewResult
import kotlinx.coroutines.flow.Flow

interface StudyRepository {
    suspend fun recordReview(cardId: String, deckId: String, result: ReviewResult)
    fun getReviewsForDeck(deckId: String): Flow<List<ReviewRecord>>
    fun getAllReviews(): Flow<List<ReviewRecord>>
    suspend fun createExam(categoryId: String): Result<String> // examId
    suspend fun startExam(examId: String): Result<ExamSession>
    suspend fun getExamReview(examId: String): Result<List<ReviewQuiz>>
    suspend fun submitResult(examLogId: String, quizId: String, result: Boolean): Result<Unit>
    suspend fun completeExam(examLogId: String): Result<Unit>
    suspend fun getExamReviewDetail(examLogId: String): Result<ExamReviewDetail>
}
